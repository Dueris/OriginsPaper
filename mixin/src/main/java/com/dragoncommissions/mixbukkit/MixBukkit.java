package com.dragoncommissions.mixbukkit;

import com.dragoncommissions.mixbukkit.addons.AutoMapper;
import com.dragoncommissions.mixbukkit.agent.ClassesManager;
import com.dragoncommissions.mixbukkit.agent.JVMAttacher;
import com.dragoncommissions.mixbukkit.api.MixinPlugin;
import com.dragoncommissions.mixbukkit.api.ObfMap;
import com.dragoncommissions.mixbukkit.utils.io.BukkitErrorOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class MixBukkit {

	public final static String VERSION = "0.1";
	public final static BuildType BUILD_TYPE = BuildType.SNAPSHOT;
	@Getter
	private static final Map<String, MixinPlugin> plugins = new HashMap<>();
	public static boolean DEBUG = BUILD_TYPE.isDevBuild();
	public static boolean WRITE_TRANSFORMED_CLASS = false;
	public static boolean SAFE_MODE = true;
	public static Instrumentation INSTRUMENTATION = null;
	public static boolean PREPARED = false;
	public static BukkitErrorOutputStream ERROR_OUTPUT_STREAM = new BukkitErrorOutputStream();
	public static ClassesManager classesManager;
	@Getter
	private static JVMAttacher jvmAttacher;
	@Getter
	private File pluginFile;

	@SneakyThrows
	public static void addLibrary(File file) {
		try {
			INSTRUMENTATION.appendToSystemClassLoaderSearch(new JarFile(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (DEBUG) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Loading " + file.getAbsolutePath());
		}
	}

	@SneakyThrows
	public void onEnable(JavaPlugin plugin, File pluginFile, URLClassLoader parent) {
		// ((URLClassLoader) plugin.getClassLoader().getParent());
		this.pluginFile = pluginFile; // plugin.getFile()

		loadConfig();

		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "=-=-=-=-= MixBukkit Loader =-=-=-=-=");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Version: " + VERSION);
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Build Type: " + BUILD_TYPE);
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "MC Version: " + AutoMapper.getMCVersion());
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Server Remapped: " + !AutoMapper.isObfuscatedBuild());
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "");
		if (!SAFE_MODE) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Warning: Safe mode is disabled! It might load invalid class and crash the Server/JVM");
		}
		if (!DEBUG) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// If you wish to see debug messages, please enable \"debug-mode\" in your config file");
		} else {
			if (!WRITE_TRANSFORMED_CLASS) {
				plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// If you wish to see transformed version of class (for testing purposes), you can enable \"write-transformed-class\" in config!");
			}
		}
		if (WRITE_TRANSFORMED_CLASS) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "// Write output class enabled! Transformed classes will be renamed and go into your temp folder.");
		}
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "~~ Started loading ~~");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + " - Attaching to JVM...");
		jvmAttacher = new JVMAttacher(this);
		jvmAttacher.attach();
		if (INSTRUMENTATION == null) {
//            plugin.setEnabled(false);
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "- Failed grabbing instrumentation! If you believe this is an issue, please open a ticket");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "======= FAILED GETTING INSTRUMENTATION ======");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please check those things before opening an issue:");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "1. Do you have -XX:+DisableAttachMechanism? If yes, remove it from server start command.");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "2. Does the server have permission to spawn a process? If no, give it. Normally yes unless you are using server panel that limits the privilege");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "");
			throw new NullPointerException("Instrumentation is null");
		}
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "- Finished Attaching!");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "- Preparing class transformers...");
		ClassesManager.init();
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "- Finished preparing class transformers!");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[!] Finished loading MixBukkit!");
		plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		PREPARED = true;
	}

	private void loadConfig() {
		try {
			SAFE_MODE = true;
			DEBUG = true;
			WRITE_TRANSFORMED_CLASS = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public @NotNull MixinPlugin registerMixinPlugin(@NotNull Plugin plugin, InputStream membersMapStream) {
		MixinPlugin mixinPlugin = plugins.get(plugin.getName());
		if (mixinPlugin != null) {
			return mixinPlugin;
		}
		mixinPlugin = new MixinPlugin(plugin, new ObfMap(membersMapStream));
		plugins.put(plugin.getName(), mixinPlugin);
		try {
			Method getFile = JavaPlugin.class.getDeclaredMethod("getFile");
			getFile.setAccessible(true);
			File pluginFile = ((File) getFile.invoke(plugin));
			pluginFile = pluginFile.getAbsoluteFile();
		} catch (Exception e) {
			e.printStackTrace();
		}


		return mixinPlugin;
	}

	@AllArgsConstructor
	@Getter
	public enum BuildType {
		SNAPSHOT(true),
		BETA(false),
		RELEASE(false);

		private final boolean devBuild;

		BuildType(boolean devBuild) {
			this.devBuild = devBuild;
		}

		public boolean isDevBuild() {
			return devBuild;
		}
	}
}
