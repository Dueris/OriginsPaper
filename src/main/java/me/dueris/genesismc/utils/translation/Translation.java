package me.dueris.genesismc.utils.translation;

import com.destroystokyo.paper.ClientOption;
import me.dueris.genesismc.files.GenesisDataFiles;
import net.minecraft.locale.Language;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Translation {

    public static String getPlayerLocale(Player player){
        CraftPlayer craftPlayer = (CraftPlayer) player;
        @NotNull String language = player.getClientOption(ClientOption.LOCALE);
        return language;
    }

    public File getPlayerLangFromLocale(String locale){
        if(LangConfig.getFile(locale) == null){
            return LangConfig.getLangFile();
        }
        return LangConfig.getFile(locale);
    }
}
