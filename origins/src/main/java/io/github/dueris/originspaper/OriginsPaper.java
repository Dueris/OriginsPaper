package io.github.dueris.originspaper;

import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.calio.parser.JsonObjectRemapper;
import io.github.dueris.originspaper.action.factory.BiEntityActions;
import io.github.dueris.originspaper.action.factory.BlockActions;
import io.github.dueris.originspaper.action.factory.EntityActions;
import io.github.dueris.originspaper.action.factory.ItemActions;
import io.github.dueris.originspaper.command.OriginCommand;
import io.github.dueris.originspaper.command.PowerCommand;
import io.github.dueris.originspaper.command.ResourceCommand;
import io.github.dueris.originspaper.condition.factory.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.OriginsDataTypes;
import io.github.dueris.originspaper.data.types.modifier.ModifierOperations;
import io.github.dueris.originspaper.loot.condition.ApoliLootConditionTypes;
import io.github.dueris.originspaper.loot.function.ApoliLootFunctionTypes;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.type.FireProjectilePower;
import io.github.dueris.originspaper.storage.OriginConfiguration;
import io.github.dueris.originspaper.util.LangFile;
import io.github.dueris.originspaper.util.Renderer;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class OriginsPaper {
	public static final Logger LOGGER = LogManager.getLogger("OriginsPaper");
	public static final AtomicReference<Path> DATAPACK_PATH = new AtomicReference<>();
	public static final AtomicReference<PackRepository> PACK_REPOSITORY = new AtomicReference<>();
	public static final AtomicReference<RegistryAccess> REGISTRY_ACCESS = new AtomicReference<>();
	public static String LANGUAGE = "en_us";
	public static boolean showCommandOutput = false;
	public static MinecraftServer server;
	public static Path jarFile;
	public static String version = "v1.2.3";

	public static @NotNull ResourceLocation identifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("originspaper", path);
	}

	public static @NotNull ResourceLocation originIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("origins", path);
	}

	public static @NotNull ResourceLocation apoliIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("apoli", path);
	}

	public static PowerType getPower(ResourceLocation location) {
		return PowerType.REGISTRY.get(location);
	}

	public static OriginLayer getLayer(ResourceLocation location) {
		return OriginLayer.REGISTRY.get(location);
	}

	public static Origin getOrigin(ResourceLocation location) {
		return Origin.REGISTRY.get(location);
	}

	public static OriginsPlugin getPlugin() {
		return OriginsPlugin.plugin;
	}

	public static void init(@NotNull BootstrapContext context) throws Throwable {
		jarFile = context.getPluginSource();

		LifecycleEventManager<BootstrapContext> lifecycleManager = context.getLifecycleManager();
		lifecycleManager.registerEventHandler((LifecycleEvents.COMMANDS.newHandler(event -> {
			PowerCommand.register(event.registrar());
			OriginCommand.register(event.registrar());
			ResourceCommand.register(event.registrar());
		})).priority(4));

		io.github.dueris.calio.parser.JsonObjectRemapper remapper = new io.github.dueris.calio.parser.JsonObjectRemapper(
			List.of(new Tuple<>("origins", "apoli")),
			List.of(
				new Tuple<>("apoli:has_tag", "apoli:has_command_tag"),
				new Tuple<>("apoli:custom_data", "apoli:nbt"),
				new Tuple<>("apoli:is_equippable", "apoli:equippable"),
				new Tuple<>("apoli:fireproof", "apoli:fire_resistant"),
				new Tuple<>("apoli:merge_nbt", "apoli:merge_custom_data"),
				new Tuple<>("apoli:revoke_power", "apoli:remove_power"),
				new Tuple<>("apoli:water_protection", "origins:water_protection"),
				new Tuple<>("apoli:enderian_pearl", "minecraft:ender_pearl")
			),
			List.of("power_type", "type", "entity_type")
		);
		JsonObjectRemapper.PRE_REMAP_HOOK.add(new Tuple<>(
			"apoli:enderian_pearl",
			(tuple) -> FireProjectilePower.IS_ENDERIAN_PEARL.add(tuple.getB())
		));
		CalioParser.REMAPPER.set(remapper);
		reload();
	}

	public static void reload() throws Throwable {
		OriginConfiguration.load();
		showCommandOutput = OriginConfiguration.getConfiguration().getBoolean("show-command-output", false);
		LANGUAGE = OriginConfiguration.getConfiguration().getString("language", LANGUAGE);
		ApoliLootConditionTypes.register();
		ApoliLootFunctionTypes.register();
		LangFile.init();
		Renderer.init();

		OriginsDataTypes.init();
		ApoliDataTypes.init();

		ModifierOperations.register();
		PowerType.register();
		EntityConditions.register();
		BiEntityConditions.register();
		ItemConditions.register();
		BlockConditions.register();
		DamageConditions.register();
		FluidConditions.register();
		BiomeConditions.register();
		EntityActions.register();
		ItemActions.register();
		BlockActions.register();
		BiEntityActions.register();
	}
}
