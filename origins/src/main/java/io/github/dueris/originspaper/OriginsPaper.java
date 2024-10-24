package io.github.dueris.originspaper;

import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.command.PowerCommand;
import io.github.dueris.originspaper.command.ResourceCommand;
import io.github.dueris.originspaper.condition.type.*;
import io.github.dueris.originspaper.entity.EnderianPearlEntity;
import io.github.dueris.originspaper.global.GlobalPowerSetManager;
import io.github.dueris.originspaper.loot.condition.ApoliLootConditionTypes;
import io.github.dueris.originspaper.loot.function.ApoliLootFunctionTypes;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.origin.OriginManager;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.plugin.PluginInstances;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.type.PowerTypes;
import io.github.dueris.originspaper.registry.ApoliClassData;
import io.github.dueris.originspaper.util.fabric.resource.FabricResourceManagerImpl;
import io.github.dueris.originspaper.util.modifier.ModifierOperations;
import io.papermc.paper.command.brigadier.PaperCommands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class OriginsPaper {
	public static final Logger LOGGER = LogManager.getLogger("OriginsPaper");
	public static String LANGUAGE = "en_us";
	public static boolean showCommandOutput = false;
	public static MinecraftServer server;
	public static Path jarFile;
	public static BootstrapContext bootContext;
	public static String version = "v1.3.0";

	public static @NotNull ResourceLocation originIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("origins", path);
	}

	public static @NotNull ResourceLocation apoliIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("apoli", path);
	}

	public static OriginsPlugin getPlugin() {
		return OriginsPlugin.plugin;
	}

	public static void init(@NotNull BootstrapContext context) throws Throwable {
		jarFile = context.getPluginSource();
		OriginsPaper.bootContext = context;
		PluginInstances.init();

		// Ideally I would like to change this to use a mixin into the '<init>' of the 'Commands' class, but
		//  Paper messes with this in terms of registering custom arguments via brigadier, so would need a way to translate
		//  Papers API Command context -> Vanilla context - Dueris
		context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
			PaperCommands contextCommands = (PaperCommands) event.registrar();
			contextCommands.register(PluginInstances.APOLI_META, PowerCommand.node(), null, new ArrayList<>());
			contextCommands.register(PluginInstances.APOLI_META, ResourceCommand.node(), null, new ArrayList<>());
		}).priority(1));
		EnderianPearlEntity.bootstrap();

		reload();
	}

	public static void reload() throws Throwable {
		OriginConfiguration.load();
		showCommandOutput = OriginConfiguration.getConfiguration().getBoolean("show-command-output", false);
		LANGUAGE = OriginConfiguration.getConfiguration().getString("language", LANGUAGE);
		ApoliLootConditionTypes.register();
		ApoliLootFunctionTypes.register();
		ApoliClassData.registerAll();

		ModifierOperations.register();
		PowerTypes.register();
		EntityConditionTypes.register();
		BiEntityConditionTypes.register();
		ItemConditionTypes.register();
		BlockConditionTypes.register();
		DamageConditionTypes.register();
		FluidConditionTypes.register();
		BiomeConditionTypes.register();
		EntityActionTypes.register();
		ItemActionTypes.register();
		BlockActionTypes.register();
		BiEntityActionTypes.register();

		FabricResourceManagerImpl.registerResourceReload(new PowerManager());
		FabricResourceManagerImpl.registerResourceReload(new GlobalPowerSetManager());
		FabricResourceManagerImpl.registerResourceReload(new OriginManager());
		FabricResourceManagerImpl.registerResourceReload(new OriginLayerManager());
		IdentifierAlias.GLOBAL.addNamespaceAlias("apoli", "calio");
		IdentifierAlias.GLOBAL.addNamespaceAlias("origins", "apoli");
		LOGGER.info("OriginsPaper, version {}, is initialized and ready to power up your game!", version);
	}

	public static class OriginConfiguration {
		private static File server;
		private static File orb;

		public static void load() throws IOException {
			File dataFolder = new File("plugins/OriginsPaper/");
			if (!dataFolder.exists()) {
				dataFolder.mkdirs();
			}

			File orbFile = fillFile("orb-of-origin.yml", new File(dataFolder, "orb-of-origin.yml"));
			File originServer = fillFile("origin-server.yml", new File(dataFolder, "origin-server.yml"));
			server = originServer;
			orb = orbFile;
			if (getConfiguration() == null) {
				throw new RuntimeException("Unable to load origin-server configuration file!");
			} else if (getOrbConfiguration() == null) {
				throw new RuntimeException("Unable to load orb configuration file!");
			} else {
				getConfiguration().addDefaults(Map.of("choosing_delay", 0));
				getOrbConfiguration().addDefaults(Map.of());
			}
		}

		private static @NotNull File fillFile(String a, @NotNull File o) throws IOException {
			if (!o.exists()) {
				o.createNewFile();
				ClassLoader cL = OriginConfiguration.class.getClassLoader();

				try (InputStream stream = cL.getResourceAsStream(a)) {
					if (stream == null) {
						throw new RuntimeException("Unable to find resource: " + a);
					}

					byte[] buffer = stream.readAllBytes();
					Files.write(o.toPath(), buffer);
				} catch (IOException var8) {
					var8.printStackTrace();
				}

			}
			return o;
		}

		public static @NotNull YamlConfiguration getConfiguration() {
			return YamlConfiguration.loadConfiguration(server);
		}

		public static @NotNull FileConfiguration getOrbConfiguration() {
			return YamlConfiguration.loadConfiguration(orb);
		}
	}
}
