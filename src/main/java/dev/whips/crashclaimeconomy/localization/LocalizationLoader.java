package dev.whips.crashclaimeconomy.localization;

import dev.whips.crashclaimeconomy.CrashClaimEconomy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;

public class LocalizationLoader {
    static MiniMessage parser;
    static MiniMessage userParser;
    static PlaceholderManager placeholderManager;

    public static void initialize(){
        parser = MiniMessage.builder()
                .parsingErrorMessageConsumer((s) -> CrashClaimEconomy.getInstance().getLogger().warning("Config MiniMessage Parsing Error: " + s))
                .build();

        userParser = MiniMessage.builder()
                .removeDefaultTransformations()
                .transformation(TransformationType.COLOR)
                .transformation(TransformationType.RESET)
                .transformation(TransformationType.DECORATION)
                .transformation(TransformationType.GRADIENT)
                .transformation(TransformationType.RAINBOW)
                .transformation(TransformationType.RAINBOW)
                .transformation(TransformationType.FONT)
                .build();

        placeholderManager = new PlaceholderManager();

        Localization.rebuildCachedMessages();
    }
}
