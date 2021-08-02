package dev.whips.crashclaimeconomy.config;

public class GlobalConfig extends BaseConfig{
    public static String locale;
    public static int initialClaimBlocks;
    public static int claimBlockReward;
    public static int claimBlockRewardMillis;

    public static boolean sendRewardAlert;
    public static int alertFadeIn;
    public static int alertDuration;
    public static int alertFadeOut;

    public static boolean sendInChatInstead;

    public static String forcedVersionString;

    private static void loadGeneral(){
        locale = getString("language", "en_US");

        initialClaimBlocks = getInt("initial-claimblocks", 100);

        claimBlockReward = getInt("reward", 25);
        claimBlockRewardMillis = getInt("reward-minutes", 60) * 60 * 1000;

        sendRewardAlert = getBoolean("send-reward-alert", true);
        alertFadeIn = getInt("reward-alert.fadeIn", 15);
        alertDuration = getInt("reward-alert.duration", 20);
        alertFadeOut = getInt("reward-alert.fadeOut", 15);
        sendInChatInstead = getBoolean("send-reward-alert-in-chat", false);

        forcedVersionString = config.getString("use-this-version-instead");
    }
}
