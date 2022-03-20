package dev.whips.crashclaimeconomy.localization;

import dev.whips.crashclaimeconomy.CrashClaimEconomy;
import dev.whips.crashclaimeconomy.config.BaseConfig;
import dev.whips.crashclaimeconomy.config.ConfigManager;
import dev.whips.crashclaimeconomy.config.GlobalConfig;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public enum Localization {
    ECONOMY__CHECK_SELF_BALANCE("<gold>ClaimBlock Balance: <yellow><balance>"),
    ECONOMY__CHECK_OTHER_BALANCE("<gold>ClaimBlock Balance for <green><username><gold>: <yellow><balance>"),
    ECONOMY__ADD_OTHER("<green>Successfully added <yellow><balance> <green>ClaimBlocks to <gold><username>"),
    ECONOMY__ADD_OTHER_ERROR("<red>Failed to add <yellow><balance> <red>ClaimBlocks to <gold><username><red> reason: <error>"),
    ECONOMY__REMOVE_OTHER("<green>Successfully removed <yellow><balance> <green>ClaimBlocks from <gold><username>"),
    ECONOMY__REMOVE_OTHER_ERROR("<red>Failed to remove <yellow><balance> <red>ClaimBlocks from <gold><username><red> reason: <error>"),

    ALERT__CHAT("<yellow><bold>+<reward></bold> ClaimBlocks Rewarded"),
    ALERT__ACTIONBAR("<yellow><bold>+<reward></bold> ClaimBlocks Rewarded"),
    ;

    private static class Utils {
        static ItemStack addItemShine(ItemStack itemStack){
            ItemStack item = itemStack.clone();
            item.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            return item;
        }
    }

    public static BaseComponent[] parseRawUserInput(String s){
        return BungeeComponentSerializer.get().serialize(LocalizationLoader.userParser.deserialize(s));
    }

    public static void rebuildCachedMessages(){
        Logger logger = CrashClaimEconomy.getInstance().getLogger();

        File languagesFolder = new File(CrashClaimEconomy.getInstance().getDataFolder(), "languages");
        languagesFolder.mkdirs();
        File languageFile = new File(languagesFolder, GlobalConfig.locale + ".yml");

        if (!languageFile.exists() && !GlobalConfig.locale.equals("en_US")){
            logger.warning("Language file does not exist, /languages/" + GlobalConfig.locale + ".yml, reverting to en_US");
            languageFile = new File(languagesFolder, "en_US.yml");
        }

        try {
            ConfigManager.initConfig(languageFile, Config.class);
        } catch (Exception ex){
            logger.severe("Language config failed to load properly. Continuing on.");
            ex.printStackTrace();
        }
    }

    void postLoad(){

    }

    private static class Config extends BaseConfig {
        private static void load(){
            for (Localization localization : Localization.values()){
                switch (localization.type){
                    case MESSAGE:
                        localization.setDefault(getString(parseToKey(localization.name()), localization.def));
                        localization.hasPlaceholders = LocalizationLoader.placeholderManager.hasPlaceholders(localization.def);

                        if (!localization.hasPlaceholders) {
                            localization.message = localization.getMessage(null, new String[0]);
                        }
                        break;
                    case MESSAGE_LIST:
                        localization.setDefaultList(getStringList(parseToKey(localization.name()), Arrays.asList(localization.defList)).toArray(new String[0]));
                        localization.hasPlaceholders = LocalizationLoader.placeholderManager.hasPlaceholders(localization.defList);

                        if (!localization.hasPlaceholders) {
                            localization.messageList = localization.getMessageList(null, new String[0]);
                        }
                        break;
                    case ITEM:
                        localization.item = createItemStack(parseToKey(localization.name()), localization.getItemTemplate());
                        break;
                    case CODE_ONLY:
                        break;
                }

                localization.postLoad();
            }
        }

        private static void removeKey(String key){
            config.set(key, null);
        }

        private static String parseToKey(String key){
            return key.replaceAll("__", ".").replaceAll("_", "-").toLowerCase();
        }

        private static ItemStackTemplate createItemStack(String key, ItemStackTemplate template){
            String title = getString(key + ".title", template.getTitle());
            List<String> lore = getStringList(key + ".lore", template.getLore());
            int model = config.getInt(key + ".model");

            return new ItemStackTemplate(
                    template.getMaterial() != null ? getMaterial(key + ".type", template.getMaterial()) : Material.PAPER, // Usually gets replaced
                    getInt(key + ".count", template.getStackSize()),
                    title,
                    lore,
                    model == 0 ? null : model,
                    itemHasPlaceholders(title, lore)
            );
        }
    }

    private static boolean itemHasPlaceholders(String title, List<String> lore){
        return LocalizationLoader.placeholderManager.hasPlaceholders(title)
                || LocalizationLoader.placeholderManager.hasPlaceholders(lore.toArray(new String[0]));
    }

    private static TagResolver generateTagResolver(String... replace){
        TagResolver.Builder builder = TagResolver.builder();
        for (int x = 0; x < replace.length - 1; x+=2){
            builder.resolver(Placeholder.parsed(replace[x], replace[x + 1]));
        }
        return builder.build();
    }

    private enum localizationType {
        MESSAGE,
        MESSAGE_LIST,
        ITEM,
        CODE_ONLY
    }

    private String def;
    private String[] defList;

    private final localizationType type;

    private boolean hasPlaceholders = false;

    Localization(){
        this.def = "";
        this.defList = null;
        this.type = localizationType.CODE_ONLY;
    }

    Localization(String def){
        this.def = def;
        this.defList = null;
        this.type = localizationType.MESSAGE;
    }

    private BaseComponent[] message;
    private List<BaseComponent[]> messageList;

    Localization(String... defList){
        this.def = null;
        this.defList = defList;
        this.type = localizationType.MESSAGE_LIST;
    }

    private ItemStackTemplate item;

    Localization(Material material, Integer stackSize, String title, String... loreDef){
        this.def = null;
        this.defList = null;
        this.type = localizationType.ITEM;

        this.item = new ItemStackTemplate(material, stackSize, title, Arrays.asList(loreDef), null, false);
    }

    public BaseComponent[] getMessage(Player player) {
        if (hasPlaceholders){
            return getMessage(player, new String[0]);
        }
        return message;
    }

    public BaseComponent[] getMessage(Player player, String... replace){
        if (hasPlaceholders){
            return BungeeComponentSerializer.get().serialize(LocalizationLoader.parser.deserialize(LocalizationLoader.placeholderManager.usePlaceholders(player, def), generateTagResolver(replace)));
        }
        return BungeeComponentSerializer.get().serialize(LocalizationLoader.parser.deserialize(def, generateTagResolver(replace)));
    }

    public List<BaseComponent[]> getMessageList(Player player) {
        if (hasPlaceholders){
            return getMessageList(player, new String[0]);
        }
        return messageList;
    }

    public List<BaseComponent[]> getMessageList(Player player, String... replace){
        ArrayList<BaseComponent[]> arr = new ArrayList<>(defList.length);

        if (hasPlaceholders){
            for (String line : defList) {
                Collections.addAll(arr, BungeeComponentSerializer.get().serialize(LocalizationLoader.parser.deserialize(
                        LocalizationLoader.placeholderManager.usePlaceholders(player, line), generateTagResolver(replace))));
            }
        } else {
            for (String line : defList) {
                Collections.addAll(arr, BungeeComponentSerializer.get().serialize(LocalizationLoader.parser.deserialize(line, generateTagResolver(replace))));
            }
        }

        return arr;
    }

    public ItemStack getItem(Player player){
        return item.build(player);
    }

    public ItemStack getItem(Player player, String... replace){
        return item.build(player, replace);
    }

    public String getRawMessage() {
        return def;
    }

    public String[] getRawList() {
        return defList;
    }

    public void setItem(ItemStackTemplate item) {
        this.item = item;
    }

    public static class ItemStackTemplate {
        private final Material material;
        private final int stackSize;
        private final String title;
        private final List<String> lore;
        private final boolean hasPlaceholders;
        private final Integer model;

        private final ItemStack staticItemStack;

        public ItemStackTemplate(Material material, int stackSize, String title, List<String> lore, Integer model, boolean hasPlaceholders) {
            this.material = material;
            this.stackSize = stackSize;
            this.title = title;
            this.lore = lore;
            this.model = model;
            this.hasPlaceholders = hasPlaceholders;

            this.staticItemStack = build(null, new String[0]);
        }

        public ItemStackTemplate(ItemStack itemStack) {
            this.material = null;
            this.stackSize = 0;
            this.title = null;
            this.lore = null;
            this.model = null;
            this.hasPlaceholders = false; // Cant have placeholders for these items.

            this.staticItemStack = itemStack;
        }

        public ItemStack build(Player player){
            if (hasPlaceholders){
                return build(player, new String[0]);
            }

            return staticItemStack;
        }

        public ItemStack build(Player player, String... replace){
            if (material == null){
                return staticItemStack;
            }

            ItemStack item = new ItemStack(material, stackSize);
            ItemMeta iMeta = item.getItemMeta();

            String newTitle = hasPlaceholders ? LocalizationLoader.placeholderManager.usePlaceholders(player, title) : title;
            iMeta.setDisplayName(BukkitComponentSerializer.legacy().serialize(Component.empty().decoration(TextDecoration.ITALIC, false).append(LocalizationLoader.parser.deserialize(newTitle, generateTagResolver(replace)))));

            if (PaperLib.isPaper()){
                List<Component> components = new ArrayList<>(lore.size());
                for (String line : lore) {
                    components.add(
                            Component.empty().decoration(TextDecoration.ITALIC, false).append(LocalizationLoader.parser.deserialize(
                                    hasPlaceholders ? LocalizationLoader.placeholderManager.usePlaceholders(player, line) : line, generateTagResolver(replace)))
                    );
                }

                iMeta.lore(components);
            } else {
                List<String> components = new ArrayList<>(lore.size());

                LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().build();
                for (String line : lore) {
                    components.add(
                            serializer.serialize(
                                    Component.empty().decoration(TextDecoration.ITALIC, false).append(LocalizationLoader.parser.deserialize(
                                            hasPlaceholders ? LocalizationLoader.placeholderManager.usePlaceholders(player, line) : line, generateTagResolver(replace))))
                    );
                }

                iMeta.setLore(components);
            }

            if (model != null){
                iMeta.setCustomModelData(model); // Set custom model data.
            }

            item.setItemMeta(iMeta);

            return item;
        }

        public Material getMaterial() {
            return material;
        }

        public int getStackSize() {
            return stackSize;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getLore() {
            return lore;
        }
    }

    private void setDefault(String def) {
        this.def = def;
    }

    private void setDefaultList(String[] defList) {
        this.defList = defList;
    }

    private void setMessage(BaseComponent[] message) {
        this.message = message;
    }

    private void setMessageList(List<BaseComponent[]> messageList) {
        this.messageList = messageList;
    }

    public ItemStackTemplate getItemTemplate() {
        return item;
    }

    private localizationType getType() {
        return type;
    }
}

