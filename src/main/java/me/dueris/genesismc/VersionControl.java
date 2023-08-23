package me.dueris.genesismc;

import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import static me.dueris.genesismc.utils.BukkitColour.GREEN;

public class VersionControl {
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
                if (versionId[0].strip().equals(pluginVersion)) pluginId = Integer.parseInt(versionId[1].strip());
                if (versionId[0].strip().equals("latest")) latestId = Integer.parseInt(versionId[1].strip());
            }

            int diff = latestId - pluginId;

            if (diff > 0)
                Bukkit.getLogger().warning("[GenesisMC] " + LangConfig.getLocalizedString("startup.versionCheck.behind").replace("%versionsBehind%", String.valueOf(diff)));
            if (diff == 0)
                Bukkit.getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString("startup.versionCheck.current")).color(TextColor.fromHexString(GREEN)));
            if (diff < 0)
                Bukkit.getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString("startup.versionCheck.ahead")).color(TextColor.fromHexString(GREEN)));

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("[GenesisMC] " + LangConfig.getLocalizedString("startup.versionCheck.fail"));
        }
    }

}
