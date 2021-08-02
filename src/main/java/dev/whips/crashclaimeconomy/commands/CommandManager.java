package dev.whips.crashclaimeconomy.commands;

import co.aikar.commands.PaperCommandManager;
import dev.whips.crashclaimeconomy.CrashClaimEconomy;

public class CommandManager {
    private final CrashClaimEconomy plugin;
    private final PaperCommandManager commandManager;

    public CommandManager(CrashClaimEconomy plugin) {
        this.plugin = plugin;
        this.commandManager = new PaperCommandManager(plugin);

        loadCommandCompletions();
        loadCommandContexts();
        loadCommands();
    }

    private void loadCommands(){
        // Commands
    }

    private void loadCommandCompletions(){
        // Command Completions
    }

    private void loadCommandContexts(){
        // Command Contexts
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }
}
