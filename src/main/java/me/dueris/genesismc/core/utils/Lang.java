package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Lang {
    public static File getLangFile(){

        String langFileName = GenesisDataFiles.getMainConfig().getString("lang");
        String filePath = GenesisMC.getPlugin().getDataFolder() + File.separator + "lang" + File.separator + langFileName + "-lang.yml";
        File langFile = new File(filePath);

        try {
            if (!langFile.exists()) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error finding lang file, please restart the server, or use a valid lang file");
                return null;
            }
        } catch (SecurityException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error accessing lang file: " + ChatColor.WHITE + e.getMessage());
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

        return null;
    }



    public static String menu_human_nothing_description = Lang.getLocalizedString("menu.original.human.nothing.description");

}
