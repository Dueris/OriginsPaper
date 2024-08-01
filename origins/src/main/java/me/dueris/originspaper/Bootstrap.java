package me.dueris.originspaper;

import com.mojang.brigadier.CommandDispatcher;
import io.github.dueris.calio.parser.CalioParser;
import net.minecraft.util.Tuple;
import io.papermc.paper.command.brigadier.ApiMirrorRootNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.PaperCommands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.dueris.originspaper.command.Commands;
import me.dueris.originspaper.content.NMSBootstrap;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.Util;
import me.dueris.originspaper.util.WrappedBootstrapContext;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Bootstrap implements PluginBootstrap {
	public static ArrayList<String> oldDV = new ArrayList<>();
	public static ArrayList<Consumer<WrappedBootstrapContext>> apiCalls = new ArrayList<>();
	public static AtomicBoolean BOOTSTRAPPED = new AtomicBoolean(false);

	static {
		oldDV.add("OriginsGenesis");
		oldDV.add("Origins-Genesis");
		oldDV.add("Origins-GenesisMC");
		oldDV.add("Origins-GenesisMC[0_2_2]");
		oldDV.add("Origins-GenesisMC[0_2_4]");
		oldDV.add("Origins-GenesisMC[0_2_6]");
	}

	public static void deleteDirectory(Path directory, boolean ignoreErrors) throws IOException {
		if (Files.exists(directory)) {
			Files.walk(directory)
				.sorted(Comparator.reverseOrder()) // Sort in reverse order for correct deletion
				.forEach(path -> {
					try {
						Files.deleteIfExists(path);
						Files.delete(path);
					} catch (IOException e) {
						if (!ignoreErrors) {
							System.err.println("Error deleting: " + path + e);
						}
					}
				});
		}
	}

	public static void copyOriginDatapack(Path datapackPath, WrappedBootstrapContext context) {
		for (String string : oldDV) {
			if (Files.exists(datapackPath)) {
				String path = Path.of(datapackPath + File.separator + string).toAbsolutePath().toString();

				try {
					deleteDirectory(Path.of(path), true);
				} catch (IOException ignore) {}
			} else {
				File file = new File(datapackPath.toAbsolutePath().toString());
				file.mkdirs();
				copyOriginDatapack(datapackPath, context);
			}
		}

		try {
			CodeSource src = Util.class.getProtectionDomain().getCodeSource();
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());

			while (true) {
				ZipEntry entry = zip.getNextEntry();
				if (entry == null) {
					zip.close();
					break;
				}

				String name = entry.getName();

				if (!name.startsWith("minecraft/")) continue;
				if (FilenameUtils.getExtension(name).equals("zip")) continue;
				if (name.equals("minecraft/")) continue;

				name = name.substring(9);
				File file = new File(datapackPath.toAbsolutePath().toString().replace(".\\", "") + File.separator + name);
				if (!file.getName().contains(".")) {
					Files.createDirectory(Path.of(file.getAbsolutePath()));
					continue;
				}

				// Ensure parent directory exists
				File parentDir = file.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				// Copy PNG files
				if (FilenameUtils.getExtension(name).equalsIgnoreCase("png")) {
					try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
						byte[] buffer = new byte[1024];
						int len;
						while ((len = zip.read(buffer)) > 0) {
							bos.write(buffer, 0, len);
						}
					}
				} else { // Copy non-PNG files as text
					Files.writeString(Path.of(file.getAbsolutePath()), new String(zip.readAllBytes()));
				}
			}
			zip.close();
		} catch (Exception e) {
			// e.printStackTrace(); // Print stack trace for debugging // I changed my mind
		}

	}

	public static String levelNameProp() {
		Path propPath = Paths.get("server.properties");
		if (propPath.toFile().exists()) {
			Properties properties = new Properties();

			try (FileInputStream input = new FileInputStream(propPath.toFile())) {
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(properties.keySet());
			return properties.getProperty("level-name", "world");
		} else {
			return "world";
		}
	}

	@Override
	public void bootstrap(@Nullable BootstrapContext bootContext) {
		WrappedBootstrapContext context = new WrappedBootstrapContext(bootContext);
		if (bootContext != null) {
			NMSBootstrap.bootstrap(context);

			for (Consumer<WrappedBootstrapContext> apiCall : apiCalls) {
				apiCall.accept(context);
			}

			File packDir = null;

			try {
				packDir = new File(this.parseDatapackPath());
				copyOriginDatapack(packDir.toPath(), context);
			} catch (Exception ignored) {
			} finally {
				if (packDir != null) {
					context.initRegistries(packDir.toPath());
				}
			}
		}

		LifecycleEventManager<BootstrapContext> lifecycleManager = context.context().getLifecycleManager();
		lifecycleManager.registerEventHandler((LifecycleEvents.COMMANDS.newHandler(event -> {
			CommandDispatcher<CommandSourceStack> commands = PaperCommands.INSTANCE.getDispatcher();
			Commands.bootstrap(((ApiMirrorRootNode) commands.getRoot()).getDispatcher());
		})).priority(10));
		io.github.dueris.calio.parser.JsonObjectRemapper remapper = new io.github.dueris.calio.parser.JsonObjectRemapper(
			List.of(
				new Tuple<>("origins", "apoli")
			),
			List.of(
				new Tuple<>("apoli:conditioned_restrict_armor", "apoli:restrict_armor"),
				new Tuple<>("apoli:has_tag", "apoli:has_command_tag"),
				new Tuple<>("apoli:custom_data", "apoli:nbt"),
				new Tuple<>("apoli:is_equippable", "apoli:equippable"),
				new Tuple<>("apoli:fireproof", "apoli:fire_resistant")
			)
		);
		CalioParser.REMAPPER.set(remapper);
		context.createRegistries(
			Registries.ORIGIN,
			Registries.LAYER,
			Registries.CRAFT_POWER,
			Registries.FLUID_CONDITION,
			Registries.ENTITY_CONDITION,
			Registries.BIOME_CONDITION,
			Registries.BIENTITY_CONDITION,
			Registries.BLOCK_CONDITION,
			Registries.ITEM_CONDITION,
			Registries.DAMAGE_CONDITION,
			Registries.ENTITY_ACTION,
			Registries.ITEM_ACTION,
			Registries.BLOCK_ACTION,
			Registries.BIENTITY_ACTION,
			Registries.LANG,
			Registries.CHOOSING_PAGE
		);
		BOOTSTRAPPED.set(true);
	}

	public String parseDatapackPath() throws Exception {
		YamlConfiguration bukkitConfiguration = YamlConfiguration.loadConfiguration(Paths.get("bukkit.yml").toFile());
		File container = new File(bukkitConfiguration.getString("settings.world-container", "."));
		String s = Optional.ofNullable(levelNameProp()).orElse("world");
		Path datapackFolder = Paths.get(container.getAbsolutePath() + File.separator + s + File.separator + "datapacks");
		return datapackFolder.toString();
	}
}
