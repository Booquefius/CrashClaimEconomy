package dev.whips.crashclaimeconomy.economy;

import co.aikar.idb.DB;
import net.crashcraft.crashpayment.payment.PaymentProvider;
import net.crashcraft.crashpayment.payment.ProviderInitializationException;
import net.crashcraft.crashpayment.payment.TransactionRecipe;
import net.crashcraft.crashpayment.payment.TransactionType;

import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public class ClaimBlockProvider implements PaymentProvider {
    @Override
    public String getProviderIdentifier() {
        return "ClaimBlockProvider";
    }

    @Override
    public boolean checkRequirements() {
        return true;
    }

    @Override
    public void setup() throws ProviderInitializationException {

    }

    @Override
    public void makeTransaction(UUID user, TransactionType type, String comment, double amount, Consumer<TransactionRecipe> callback) {
        int realAmount = (int) Math.ceil(amount);

        switch (type){
            case WITHDRAW:
                getBalance(user, (bal) -> {
                    if (realAmount > bal){
                        callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Transaction", "Insufficient Funds"));
                    } else {
                        try {
                            DB.executeUpdate("REPLACE INTO claimblocks(amount, player_id) VALUES(?, (SELECT id FROM players WHERE uuid = ?))",
                                    bal - realAmount,
                                    user);

                            callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Withdraw"));
                        } catch (SQLException e){
                            e.printStackTrace();
                            callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Transaction", "Database Error"));
                        }
                    }
                });
                break;
            case DEPOSIT:
                getBalance(user, (bal) -> {
                    try {
                        DB.executeUpdate("REPLACE INTO claimblocks(amount, player_id) VALUES(?, (SELECT id FROM players WHERE uuid = ?))",
                                bal + realAmount,
                                user.toString());

                        callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Deposit"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        callback.accept(new TransactionRecipe(user, realAmount, "ClaimBlock Transaction", "Database Error"));
                    }
                });
                break;
        }
    }

    @Override
    public void getBalance(UUID user, Consumer<Double> callback) {
        DB.getFirstColumnAsync("SELECT amount FROM claimblocks WHERE player_id = (SELECT id FROM players WHERE uuid = ?)", user.toString()).thenAccept((bal) -> {
            if (bal == null){
                callback.accept(0D);
            } else {
                int balance = (int) bal;
                callback.accept((double) balance);
            }
        }).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
    }
}
