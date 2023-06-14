package me.dueris.genesismc.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class VersionControl {

    public static boolean isCompatiblePaper() {

        try {
            Class.forName("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent");
            return true;
        } catch (ClassNotFoundException e) {
            getLogger().severe("This server is unable to start GenesisMC, disabling..");
            getServer().getPluginManager().disablePlugin(GenesisMC.getPlugin());
            return false;
        }

    }

    public static final String REQUIRED_VERSION = "1.20";

    public static boolean isCorrectVersion() {
        String serverVersion = Bukkit.getVersion();
        return serverVersion.contains(REQUIRED_VERSION);
    }

    public static void pluginVersionCheck() {
        String pluginVersion = "0.2.1";

        try {
            URL url = new URL("https://raw.githubusercontent.com/Dueris/GenesisMC/origin/version.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder builder = new StringBuilder();

            while (true) {
                String line = br.readLine();
                if (line == null) break;
                builder.append(line);
            }

            String data = builder.toString();
            int pluginId = 0;
            int latestId = 0;

            String[] var0 = data.split("\\|");
            for (String var1 : var0) {
                String[] versionId = var1.split(":");
                if (versionId[0].equals(pluginVersion)) pluginId = Integer.parseInt(versionId[1]);
                if (versionId[0].equals("latest")) latestId = Integer.parseInt(versionId[1]);
            }

            int diff = latestId - pluginId;

            if (diff > 0) Bukkit.getLogger().warning("[GenesisMC] You are running a version of the plugin that is "+diff+" versions out of date!\n    Please install the latest version from https://modrinth.com/plugin/genesismc/versions");
            if (diff == 0) Bukkit.getConsoleSender().sendMessage(Component.text("[GenesisMC] You are running the latest version of the plugin!").color(TextColor.color(0, 200, 0)));
            if (diff < 0) Bukkit.getConsoleSender().sendMessage(Component.text("[GenesisMC] You are running a dev build! Join our discord server at https://discord.gg/RKmQnU6SRt or open an issue on github for any feedback :)").color(TextColor.color(0, 200, 0)));

        } catch (Exception e) {
            Bukkit.getLogger().warning("[GenesisMC] Failed to connect to version control website!\n    You may be using an outdated version of the plugin!\n    You can install the latest version from https://modrinth.com/plugin/genesismc/versions");
        }
    }

}
