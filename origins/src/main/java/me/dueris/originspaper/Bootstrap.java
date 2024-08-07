package me.dueris.originspaper;

import com.mojang.brigadier.CommandDispatcher;
import io.github.dueris.calio.parser.CalioParser;
import io.papermc.paper.command.brigadier.ApiMirrorRootNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.PaperCommands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.dueris.originspaper.command.Commands;
import me.dueris.originspaper.content.NMSBootstrap;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.WrappedBootstrapContext;
import net.minecraft.util.Tuple;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Bootstrap implements PluginBootstrap {
	public static ArrayList<Consumer<WrappedBootstrapContext>> apiCalls = new ArrayList<>();
	public static AtomicBoolean BOOTSTRAPPED = new AtomicBoolean(false);

	public static void copyOriginDatapack(Path datapackPath) {
		String jarPath = getJarPath();
		if (jarPath == null) {
			System.err.println("Could not determine JAR file path.");
			return;
		}
		String outputDir = datapackPath.toAbsolutePath().toString();
		String resourceDir = "minecraft/";
		try {
			extractResources(jarPath, outputDir, resourceDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Resources extracted successfully.");
	}

	public static void extractResources(String jarPath, String outputDir, String resourceDir) throws IOException {
		try (JarFile jar = new JarFile(jarPath)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().startsWith(resourceDir) && !entry.isDirectory()) {
					File file = new File(outputDir, entry.getName().substring(resourceDir.length()));
					File parent = file.getParentFile();
					if (parent != null && !parent.exists()) {
						parent.mkdirs();
					}
					try (InputStream is = jar.getInputStream(entry);
						 OutputStream os = Files.newOutputStream(file.toPath())) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = is.read(buffer)) != -1) {
							os.write(buffer, 0, bytesRead);
						}
					}
				}
			}
		}
	}

	public static @Nullable String getJarPath() {
		ProtectionDomain protectionDomain = Bootstrap.class.getProtectionDomain();
		File jarFile = null;
		try {
			jarFile = new File(protectionDomain.getCodeSource().getLocation().toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jarFile != null ? jarFile.getAbsolutePath() : null;
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
				copyOriginDatapack(packDir.toPath());
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
				new Tuple<>("apoli:restrict_armor", "apoli:conditioned_restrict_armor"),
				new Tuple<>("apoli:has_tag", "apoli:has_command_tag"),
				new Tuple<>("apoli:custom_data", "apoli:nbt"),
				new Tuple<>("apoli:is_equippable", "apoli:equippable"),
				new Tuple<>("apoli:fireproof", "apoli:fire_resistant"),
				new Tuple<>("apoli:merge_nbt", "apoli:merge_custom_data"),
				new Tuple<>("apoli:revoke_power", "apoli:remove_power"),
				new Tuple<>("apoli:water_protection", "origins:water_protection") // fix water protection in namespace aliases
			),
			List.of(
				"power", "power_type", "type"
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

	public String parseDatapackPath() {
		YamlConfiguration bukkitConfiguration = YamlConfiguration.loadConfiguration(Paths.get("bukkit.yml").toFile());
		File container = new File(bukkitConfiguration.getString("settings.world-container", "."));
		String s = Optional.ofNullable(levelNameProp()).orElse("world");
		Path datapackFolder = Paths.get(container.getAbsolutePath() + File.separator + s + File.separator + "datapacks");
		return datapackFolder.toString();
	}
}
