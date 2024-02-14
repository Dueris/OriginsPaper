package me.dueris.genesismc;

import me.dueris.genesismc.util.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class VersionControl {
    public static final String REQUIRED_VERSION = "1.20";

    public static boolean isCorrectVersion() {
        String serverVersion = Bukkit.getVersion();
        return serverVersion.contains(REQUIRED_VERSION);
    }

    public static void pluginVersionCheck() {
        String pluginVersion = "0.2.7";

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
                if (versionId[0].strip().equals(pluginVersion)) pluginId = Integer.parseInt(versionId[1].strip());
                if (versionId[0].strip().equals("latest")) latestId = Integer.parseInt(versionId[1].strip());
            }

            int diff = latestId - pluginId;

            if (diff > 0)
                Bukkit.getLogger().warning("  " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.versionCheck.behind").replace("%versionsBehind%", String.valueOf(diff)));
            if (diff < 0)
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "  " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.versionCheck.ahead"));

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("  " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.versionCheck.fail"));
        }
    }

}
