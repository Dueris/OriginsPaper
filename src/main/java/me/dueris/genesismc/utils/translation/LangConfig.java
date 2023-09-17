package me.dueris.genesismc.utils.translation;

import com.mojang.datafixers.kinds.IdF;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.BukkitColour;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;


public class LangConfig {
    public static String lang_test = getLocalizedString(Bukkit.getConsoleSender(), "lang.test");

    public static File getLangFile() {

        String fileName = GenesisDataFiles.getMainConfig().getString("lang");

        if (fileName .equals("english")) fileName = "en_us";
        if (fileName .equals("german")) fileName = "de_DE";
        if (fileName .equals("russian")) fileName = "ru_RU";
        if (fileName .equals("trad-chinese")) fileName = "zh_TW";

        String filePath = GenesisMC.getPlugin().getDataFolder() + File.separator + "lang" + File.separator + fileName + ".yml";
        File langFile = new File(filePath);

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
            YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            String value =  langConfig.getString(key);
            if (value != null) return value;
            return "There was a problem reading the lang file.";
        }

        File engLang = new File(GenesisMC.getPlugin().getDataFolder() + File.separator + "lang" + File.separator + "en_us.yml");
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(engLang);
        String response = langConfig.getString(key);
        if (response == null) return "Lang Error!";
        return response;
    }

}
