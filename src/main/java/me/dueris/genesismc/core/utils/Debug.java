package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.factory.handlers.CustomOriginExistCheck;
import me.dueris.genesismc.core.factory.powers.Powers;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import static me.dueris.genesismc.core.GenesisMC.*;
import static me.dueris.genesismc.core.utils.BukkitUtils.printValues;
import static org.bukkit.Bukkit.getServer;

public class Debug {

    public static void executeGenesisDebug(){
        getServer().getConsoleSender().sendMessage("Executing Genesis Debug..");

        FileConfiguration mainConfig = GenesisDataFiles.getMainConfig();
        ConfigurationSection mainRootSection = mainConfig.getConfigurationSection("");
        printValues(mainRootSection, "");

        FileConfiguration orbConfig = GenesisDataFiles.getOrbCon();
        ConfigurationSection orbRootSection = mainConfig.getConfigurationSection("");
        printValues(orbRootSection, "");

        GenesisDataFiles.getOrbCon().getValues(Boolean.parseBoolean("all"));
        getServer().getConsoleSender().sendMessage(Lang.getLangFile().toString());
        getServer().getConsoleSender().sendMessage("Executing Server-data dump" +
                Bukkit.getAllowEnd() +
                Bukkit.getAllowNether() +
                Bukkit.getTPS() +
                Bukkit.getVersion() +
                Bukkit.getServer().getWorldType() +
                Arrays.stream(getServer().getPluginManager().getPlugins()).toArray().toString() +
                Bukkit.getServer().getMaxWorldSize() +
                Bukkit.getMinecraftVersion()
                );
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Lang.getLangFile();
                } catch (Exception e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Couldnt load lang file. Disabling GenesisMC...");
                    Bukkit.getServer().getPluginManager().disablePlugin(GenesisMC.getPlugin());
                }

                this.cancel();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 20L, 1L);

    }

    public static void executeGenesisReload(){
        Powers.loadPowers();
        try {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + Lang.lang_test);
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "A fatal error has occurred, lang could not be loaded. Disabling GenesisMC....");
            getServer().getPluginManager().disablePlugin(getPlugin());
        }
        for (String originTag : CraftApoli.getTags()) {
            if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                getServer().getConsoleSender().sendMessage("[GenesisMC] Loaded \"" + CraftApoli.getOriginName(originTag) + "\"");
            }
        }
        if (CraftApoli.getCustomOrigins().size() > 0) {
            getServer().getConsoleSender().sendMessage("[GenesisMC] Loaded (" + CraftApoli.getCustomOrigins().size() + ") Custom Origins");
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Origins Reloaded.");
            if (p.isOp()) {
                p.sendMessage(ChatColor.BLUE + "Origins Reloaded.");
            }
            CustomOriginExistCheck.customOriginExistCheck(p);
        }
    }

    public static void testIncompatiblePlugins(){
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Origins-Bukkit")) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Unable to start plugin due to Origins Bukkit being present. Using both will cause errors.");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            Bukkit.getServer().getPluginManager().disablePlugin(getPlugin());
        }
    }

    public static void versionTest(){
        boolean isCraftBukkit = false;
        boolean isSpigot = false;
        boolean isPaper = false;
        boolean isFolia = false;
        try {
            Class.forName("org.bukkit.craftbukkit.CraftServer");
            isCraftBukkit = true;
        } catch (ClassNotFoundException e) {
            // CraftBukkit class not found, not a CraftBukkit server
        }
        try {
            Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer");
            isSpigot = true;
        } catch (ClassNotFoundException e) {
            // SpigotConfig class not found, not a Spigot server
        }

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            //not folia
        }

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            // PaperConfig class not found, not a Paper server
        }
        if (isCraftBukkit) {
            // CraftBukkit server
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please use a PaperMC server. CraftBukkit server found");
        } else if (isSpigot) {
            // Spigot server
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please use a PaperMC server. SpigotMC server found");
        } else if (isPaper) {
            // Paper server
        } else if (isFolia) {
            //Folia server
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please use a PaperMC server. There is a Folia build on the modrinth page, please use that");
        }
    }
}
