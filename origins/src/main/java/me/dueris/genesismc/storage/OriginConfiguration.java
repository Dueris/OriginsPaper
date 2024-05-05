package me.dueris.genesismc.storage;

import me.dueris.genesismc.GenesisMC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class OriginConfiguration {
	private static File server;
	private static File orb;

	public static void load() throws IOException {
		JavaPlugin plugin = GenesisMC.getPlugin();
		if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
		File orbFile = fillFile("orb-of-origin.yml", new File(plugin.getDataFolder(), "orb-of-origin.yml"));
		File originServer = fillFile("origin-server.yml", new File(plugin.getDataFolder(), "origin-server.yml"));
		server = originServer;
		orb = orbFile;
		if (getConfiguration() == null) throw new RuntimeException("Unable to load origin-server configuration file!");
		if (getOrbConfiguration() == null) throw new RuntimeException("Unable to load orb configuration file!");

		getConfiguration().addDefaults(Map.of("choosing_delay", 0));
		getOrbConfiguration().addDefaults(Map.of());
	}

	private static File fillFile(String a, File o) throws IOException {
		if (o.exists()) return o;
		else o.createNewFile();
		ClassLoader cL = OriginConfiguration.class.getClassLoader();
		try (InputStream stream = cL.getResourceAsStream(a)) {
			if (stream == null) throw new RuntimeException("Unable to find resource: " + a);
			byte[] buffer = stream.readAllBytes();

			Files.write(o.toPath(), buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return o;
	}

	public static YamlConfiguration getConfiguration() {
		return YamlConfiguration.loadConfiguration(server);
	}

	public static FileConfiguration getOrbConfiguration() {
		return YamlConfiguration.loadConfiguration(orb);
	}

}
