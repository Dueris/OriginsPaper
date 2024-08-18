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
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

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
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(MixBukkit.class);
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
	public void onEnable(Logger logger, File pluginFile, URLClassLoader parent) {
		// ((URLClassLoader) plugin.getClassLoader().getParent());
		this.pluginFile = pluginFile; // plugin.getFile()

		loadConfig();

		logger.info("=-=-=-=-= MIXIN LOADER =-=-=-=-=");
		logger.info(" Starting MIXIN loader on server:");
		logger.info(" - Version: " + VERSION);
		logger.info(" - Build Type: " + BUILD_TYPE);
		logger.info(" - MC Version: " + AutoMapper.getMCVersion());
		logger.info("");

		logger.info("Attaching to JVM...");
		jvmAttacher = new JVMAttacher(this);
		jvmAttacher.attach();
		if (INSTRUMENTATION == null) {
			try {
				logger.warn("Failed grabbing instrumentation! If you believe this is an issue, please open a ticket");
				logger.warn("");
				logger.warn("======= FAILED GETTING INSTRUMENTATION ======");
				logger.warn("Please check those things before opening an issue:");
				logger.warn("1. Do you have -XX:+DisableAttachMechanism? If yes, remove it from server start command.");
				logger.warn("2. Does the server have permission to spawn a process? If no, give it. Normally yes unless you are using a server panel that limits the privilege");
				logger.warn("");

				throw new NullPointerException("Instrumentation is null");
			} catch (Exception e) {
				logger.error("An error occurred:", e);
			}
		}

		logger.info("- Finished Attaching!");
		logger.info("- Preparing class transformers...");

		ClassesManager.init();

		logger.info("- Finished preparing class transformers!");
		logger.info("");
		logger.info("Finished loading MIXIN!");
		logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
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
