package me.dueris.genesismc.util;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.storage.GenesisDataFiles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;


public class LangConfig {
    public static String lang_test = null;
    private static YamlConfiguration langConfig = null;

    public static File getLangFile() {

        String fileName = GenesisDataFiles.getMainConfig().getString("lang");

        if (fileName.equals("english")) fileName = "en_us";
        if (fileName.equals("german")) fileName = "de_DE";
        if (fileName.equals("russian")) fileName = "ru_RU";
        if (fileName.equals("trad-chinese")) fileName = "zh_TW";

        String filePath = GenesisMC.getPlugin().getDataFolder() + File.separator + "lang" + File.separator + fileName + ".yml";
        File langFile = new File(filePath);
        if (langConfig == null) {
            System.out.println("Loading LangConfiguration...");
            langConfig = YamlConfiguration.loadConfiguration(langFile);
            lang_test = getLocalizedString(Bukkit.getConsoleSender(), "lang.test");
        }

        try {
            if (!langFile.exists()) {
                Bukkit.getServer().getConsoleSender().sendMessage(Component.text("Error finding lang file, please restart the server, or use a valid lang file").color(TextColor.fromHexString(BukkitColour.RED)));
                return null;
            }
        } catch (SecurityException e) {
            return null;
        }

        return langFile;

    }

    public static File getFile(String string) {
        return GenesisDataFiles.getFile(string);
    }

    public static String getLocalizedString(CommandSender P, String key) {
        Translation translation = new Translation();
        File langFile = translation.getPlayerLangFromLocale(Translation.getPlayerLocale(P));

        if (langFile != null) {
            String value = langConfig.getString(key);
            if (value != null) return value;
            return "There was a problem reading the lang file.";
        } else {
            return "Lang Error!";
        }
    }

}
