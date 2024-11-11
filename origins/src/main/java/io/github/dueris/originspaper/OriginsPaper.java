package io.github.dueris.originspaper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.command.OriginCommand;
import io.github.dueris.originspaper.command.PowerCommand;
import io.github.dueris.originspaper.command.ResourceCommand;
import io.github.dueris.originspaper.condition.type.*;
import io.github.dueris.originspaper.content.entity.EnderianPearlEntity;
import io.github.dueris.originspaper.data.ApoliDataHandlers;
import io.github.dueris.originspaper.global.GlobalPowerSetManager;
import io.github.dueris.originspaper.loot.condition.ApoliLootConditionTypes;
import io.github.dueris.originspaper.loot.function.ApoliLootFunctionTypes;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayerManager;
import io.github.dueris.originspaper.origin.OriginManager;
import io.github.dueris.originspaper.plugin.OriginsPlugin;
import io.github.dueris.originspaper.plugin.PluginInstances;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.type.PowerTypes;
import io.github.dueris.originspaper.power.type.origins.OriginsPowerTypes;
import io.github.dueris.originspaper.registry.ApoliClassData;
import io.github.dueris.originspaper.registry.ModBlocks;
import io.github.dueris.originspaper.registry.ModItems;
import io.github.dueris.originspaper.registry.ModTags;
import io.github.dueris.originspaper.util.ChoseOriginCriterion;
import io.github.dueris.originspaper.util.GainedPowerCriterion;
import io.github.dueris.originspaper.util.OriginLootCondition;
import io.github.dueris.originspaper.util.fabric.resource.FabricResourceManagerImpl;
import io.github.dueris.originspaper.util.modifier.ModifierOperations;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.skillcode.jsonconfig.JsonConfig;
import net.skillcode.jsonconfig.JsonConfigAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

@SuppressWarnings("UnstableApiUsage")
public class OriginsPaper {
	public static final Logger LOGGER = LogManager.getLogger("OriginsPaper");
	public static MinecraftServer server;
	public static Path jarFile;
	public static BootstrapContext context;
	public static String version = "v1.3.0";
	public static OriginsPaper.ServerConfig config;

	public static @NotNull ResourceLocation identifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("origins", path);
	}

	public static @NotNull ResourceLocation apoliIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("apoli", path);
	}

	public static OriginsPlugin getPlugin() {
		return OriginsPlugin.plugin;
	}

	public static void initialize(@NotNull BootstrapContext context) {
		jarFile = context.getPluginSource();
		OriginsPaper.context = context;
		PluginInstances.init();

		CraftCalio.initialize();

		final JsonConfigAPI jsonConfigAPI = new JsonConfigAPI(true);
		File serverJson = new File(context.getDataDirectory().toFile(), "origins_server.json");
		String parentPath = context.getDataDirectory().toFile().getAbsolutePath() + File.separator;

		jsonConfigAPI.registerConfig(
			new OriginsPaper.ServerConfig(),
			parentPath,
			serverJson.getName()
		);
		config = jsonConfigAPI.getConfig(ServerConfig.class);

		ApoliLootConditionTypes.register();
		ApoliLootFunctionTypes.register();

		ApoliClassData.registerAll();

		ModifierOperations.register();
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

		PowerTypes.register();
		OriginsPowerTypes.register();
		Origin.init();

		ApoliDataHandlers.register();

		FabricResourceManagerImpl.registerResourceReload(new PowerManager());
		FabricResourceManagerImpl.registerResourceReload(new GlobalPowerSetManager());
		FabricResourceManagerImpl.registerResourceReload(new OriginManager());
		FabricResourceManagerImpl.registerResourceReload(new OriginLayerManager());

		IdentifierAlias.GLOBAL.addNamespaceAlias("apoli", "calio");
		IdentifierAlias.GLOBAL.addNamespaceAlias("origins", "apoli");

		ModBlocks.register();
		ModTags.register();
		ModItems.register();
		EnderianPearlEntity.bootstrap();

		context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
			event.registrar().register(PluginInstances.APOLI_META, PowerCommand.node(), null, new ArrayList<>());
			event.registrar().register(PluginInstances.APOLI_META, ResourceCommand.node(), null, new ArrayList<>());
		}));
		context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> event.registrar().register(context.getPluginMeta(), OriginCommand.node(), null, new ArrayList<>())));

		CriteriaTriggers.register(GainedPowerCriterion.ID.toString(), GainedPowerCriterion.INSTANCE);
		CriteriaTriggers.register(ChoseOriginCriterion.ID.toString(), ChoseOriginCriterion.INSTANCE);
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, identifier("origin"), OriginLootCondition.TYPE);
		LOGGER.info("OriginsPaper, version {}, is initialized and ready to power up your game!", version);
	}

	public static class ServerConfig implements JsonConfig {

		public String apiKey = "";

		public ExecuteCommand executeCommand = new ExecuteCommand();

		public ModifyPlayerSpawnPower modifyPlayerSpawnPower = new ModifyPlayerSpawnPower();
		public JsonObject origins = new JsonObject();

		public boolean isOriginDisabled(@NotNull ResourceLocation originId) {
			String idString = originId.toString();
			if (!origins.has(idString)) {
				return false;
			}
			JsonElement element = origins.get(idString);
			if (element instanceof JsonObject jsonObject) {
				return !GsonHelper.getAsBoolean(jsonObject, "enabled", true);
			}
			return false;
		}

		public boolean isPowerDisabled(@NotNull ResourceLocation originId, ResourceLocation powerId) {
			String originIdString = originId.toString();
			if (!origins.has(originIdString)) {
				return false;
			}
			String powerIdString = powerId.toString();
			JsonElement element = origins.get(originIdString);
			if (element instanceof JsonObject jsonObject) {
				return !GsonHelper.getAsBoolean(jsonObject, powerIdString, true);
			}
			return false;
		}

		public boolean addToConfig(@NotNull Origin origin) {
			boolean changed = false;
			String originIdString = origin.getId().toString();
			JsonObject originObj;
			if (!origins.has(originIdString) || !(origins.get(originIdString) instanceof JsonObject)) {
				originObj = new JsonObject();
				origins.add(originIdString, originObj);
				changed = true;
			} else {
				originObj = (JsonObject) origins.get(originIdString);
			}
			if (!originObj.has("enabled") || !(originObj.get("enabled") instanceof JsonPrimitive)) {
				originObj.addProperty("enabled", Boolean.TRUE);
				changed = true;
			}
			for (Power power : origin.getPowers()) {
				String powerIdString = power.getId().toString();
				if (!originObj.has(powerIdString) || !(originObj.get(powerIdString) instanceof JsonPrimitive)) {
					originObj.addProperty(powerIdString, Boolean.TRUE);
					changed = true;
				}
			}
			return changed;
		}

		public static class ExecuteCommand {
			public int permissionLevel = 2;
			public boolean showOutput = false;
		}

		public static class ModifyPlayerSpawnPower {
			public int radius = 6400;
			public int horizontalBlockCheckInterval = 64;
			public int verticalBlockCheckInterval = 64;
		}
	}
}
