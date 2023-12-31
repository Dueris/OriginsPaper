package me.dueris.genesismc.utils;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.PlayerHandler;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import static me.dueris.genesismc.GenesisMC.getPlugin;
import static me.dueris.genesismc.utils.BukkitUtils.printValues;
import static org.bukkit.Bukkit.getServer;

public class Debug {

    public static void executeGenesisDebug() {
        Bukkit.getServer().getConsoleSender().sendMessage("* (-debugOrigins={true}) || BEGINNING DEBUG {");
        Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @1 powers".replace("@1", String.valueOf(CraftPower.getRegistered().toArray().length)));
        Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @2 origins = [".replace("@2", String.valueOf(CraftApoli.getOrigins().toArray().length)));
            for(OriginContainer originContainer : CraftApoli.getOrigins()){
                Bukkit.getServer().getConsoleSender().sendMessage("     () -> {@3}".replace("@3", originContainer.tag.asString()));
            }
        Bukkit.getServer().getConsoleSender().sendMessage("  ]");
        Bukkit.getServer().getConsoleSender().sendMessage("  - Power thread starting with {originScheduler}".replace("originScheduler", GenesisMC.scheduler.toString()));
        Bukkit.getServer().getConsoleSender().sendMessage("  - Lang testing = {true}");
        Bukkit.getServer().getConsoleSender().sendMessage("}");
    }
}
