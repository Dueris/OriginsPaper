package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static String lang_test = getLocalizedString("lang.test");

    public static File getLangFile() {

        String langFileName = GenesisDataFiles.getMainConfig().getString("lang");
        String filePath = GenesisMC.getPlugin().getDataFolder() + File.separator + "lang" + File.separator + langFileName + ".yml";
        File langFile = new File(filePath);

        try {
            if (!langFile.exists()) {
                Bukkit.getServer().getConsoleSender().sendMessage(Component.text("Error finding lang file, please restart the server, or use a valid lang file").color(TextColor.fromHexString(BukkitColour.RED)));
                return null;
            }
        } catch (SecurityException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Component.text("Error accessing lang file:\n" + e.getMessage()).color(TextColor.fromHexString(BukkitColour.RED)));
            return null;
        }

        return langFile;

    }

    public static String getLocalizedString(String key) {
        File langFile = getLangFile();

        if (langFile != null) {
            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            return langConfig.getString(key);
        }

        File engLang = new File(GenesisMC.getPlugin().getDataFolder() + File.separator + "lang" + File.separator + "english.yml");
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(engLang);
        String response = langConfig.getString(key);
        if (response == null) return "Lang Error!";
        return response;
    }

}
