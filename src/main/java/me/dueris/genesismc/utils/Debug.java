package me.dueris.genesismc.utils;

import me.dueris.genesismc.FoliaOriginScheduler;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

import static me.dueris.genesismc.utils.BukkitUtils.printValues;
import static org.bukkit.Bukkit.getServer;

public class Debug {

    public static void executeGenesisDebug() {
        getServer().getConsoleSender().sendMessage(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.debug.start"));

        FileConfiguration mainConfig = GenesisDataFiles.getMainConfig();
        ConfigurationSection mainRootSection = mainConfig.getConfigurationSection("");
        printValues(mainRootSection, "");

        FileConfiguration orbConfig = GenesisDataFiles.getOrbCon();
        ConfigurationSection orbRootSection = mainConfig.getConfigurationSection("");
        printValues(orbRootSection, "");

        GenesisDataFiles.getOrbCon().getValues(Boolean.parseBoolean("all"));
        getServer().getConsoleSender().sendMessage(LangConfig.getLangFile().toString());
        getServer().getConsoleSender().sendMessage(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.debug.dump") +
                Bukkit.getAllowEnd() +
                Bukkit.getAllowNether() +
                Bukkit.getTPS() +
                Bukkit.getVersion() +
                Bukkit.getServer().getWorldType() +
                Arrays.stream(getServer().getPluginManager().getPlugins()).toArray().toString() +
                Bukkit.getServer().getMaxWorldSize() +
                Bukkit.getMinecraftVersion()
        );
        FoliaOriginScheduler.getGlobalScheduler().runTaskLater(new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    LangConfig.getLangFile();
                } catch (Exception e) {
                    Bukkit.getServer().getLogger().warning("Couldn't load lang file. Disabling GenesisMC...");
                    Bukkit.getServer().getPluginManager().disablePlugin(GenesisMC.getPlugin());
                }
            }
        }, 20);
    }

    public static void executeGenesisReload() {
//        try {
//            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + Lang.lang_test);
//        } catch (Exception e) {
//            getServer().getConsoleSender().sendMessage(ChatColor.RED + "A fatal error has occurred, lang could not be loaded. Disabling GenesisMC....");
//            getServer().getPluginManager().disablePlugin(getPlugin());
//        }
//        CraftApoli.loadOrigins();
//        for (OriginContainer origins : CraftApoli.getOrigins()) {
//            if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
//                getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Loaded \"" + origins.getName() + "\"").color(TextColor.color(0, 200, 0)));
//            }
//        }
//        if (CraftApoli.getOrigins().size() > 0) {
//            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Loaded (" + CraftApoli.getOrigins().size() + ") Origins").color(TextColor.color(0, 200, 0)));
//        }
//        for (Player p : Bukkit.getOnlinePlayers()) {
//            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Origins Reloaded.");
//            if (p.isOp()) {
//                p.sendMessage(ChatColor.BLUE + "Origins Reloaded.");
//            }
//            PlayerHandler.originValidCheck(p);
//        }
    }

    public static void versionTest() {
        boolean isCraftBukkit = false;
        boolean isSpigot = false;
        boolean isPaper = false;
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
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            // PaperConfig class not found, not a Paper server
        }
        if (isCraftBukkit || isSpigot) {
            Bukkit.getServer().getLogger().warning(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.debug.server"));
        }
    }
}
