package io.github.dueris.originspaper.data;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.types.*;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ApoliDataTypes {
	public static final SerializableDataBuilder<ActionFactory<Entity>> ENTITY_ACTION = action(Registries.ENTITY_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Tuple<Entity, Entity>>> BIENTITY_ACTION = action(Registries.BIENTITY_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Triple<Level, BlockPos, Direction>>> BLOCK_ACTION = action(Registries.BLOCK_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Tuple<Level, SlotAccess>>> ITEM_ACTION = action(Registries.ITEM_ACTION);
	public static final SerializableDataBuilder<ConditionFactory<Tuple<Entity, Entity>>> BIENTITY_CONDITION = condition(Registries.BIENTITY_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<Holder<Biome>>> BIOME_CONDITION = condition(Registries.BIOME_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<BlockInWorld>> BLOCK_CONDITION = condition(Registries.BLOCK_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<Tuple<DamageSource, Float>>> DAMAGE_CONDITION = condition(Registries.DAMAGE_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<Entity>> ENTITY_CONDITION = condition(Registries.ENTITY_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<Tuple<Level, ItemStack>>> ITEM_CONDITION = condition(Registries.ITEM_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<FluidState>> FLUID_CONDITION = condition(Registries.FLUID_CONDITION);
	public static final SerializableDataBuilder<Space> SPACE = SerializableDataTypes.enumValue(Space.class);
	public static final SerializableDataBuilder<ResourceOperation> RESOURCE_OPERATION = SerializableDataTypes.enumValue(ResourceOperation.class);
	public static final SerializableDataBuilder<InventoryType> INVENTORY_TYPE = SerializableDataTypes.enumValue(InventoryType.class);
	public static final SerializableDataBuilder<Util.ProcessMode> PROCESS_MODE = SerializableDataTypes.enumValue(Util.ProcessMode.class);
	public static final SerializableDataBuilder<Keybind> KEYBIND = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
				return new Keybind(SerializableDataTypes.STRING.deserialize(jsonElement), false);
			} else if (jsonElement.isJsonObject()) {
				JsonObject jo = jsonElement.getAsJsonObject();
				String key = SerializableDataTypes.STRING.deserialize(jo.get("key"));
				boolean continuous = jo.has("continuous") ? SerializableDataTypes.BOOLEAN.deserialize(jo.get("continuous")) : false;
				return new Keybind(key, continuous);
			} else throw new JsonSyntaxException("Keybind must be an instanceof a JsonObject!");
		}, Keybind.class
	);
	public static final SerializableDataBuilder<AttributedEntityAttributeModifier> ATTRIBUTED_ATTRIBUTE_MODIFIER = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo))
				throw new JsonSyntaxException("Expected a JsonObject for Attributed Attribute Modifier");
			return new AttributedEntityAttributeModifier(
				SerializableDataTypes.ATTRIBUTE_ENTRY.deserialize(jo.get("attribute")),
				SerializableDataTypes.ATTRIBUTE_MODIFIER.deserialize(jo)
			);
		}, AttributedEntityAttributeModifier.class
	);
	public static final SerializableDataBuilder<Tuple<Integer, net.minecraft.world.item.ItemStack>> POSITIONED_ITEM_STACK = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo))
				throw new JsonSyntaxException("Expected JsonObject for Positioned ItemStack!");
			return new Tuple<>(
				jo.has("slot") ? SerializableDataTypes.INT.deserialize(jo.get("slot")) : Integer.MIN_VALUE,
				SerializableDataTypes.ITEM_STACK.deserialize(jo));
		}, Tuple.class
	);
	public static final SerializableDataBuilder<Comparison> COMPARISON = SerializableDataTypes.enumValue(Comparison.class, Util.buildEnumMap(Comparison.class, Comparison::getComparisonString));
	public static final SerializableDataBuilder<ArgumentWrapper<Integer>> ITEM_SLOT = SerializableDataTypes.argumentType(SlotArgument.slot());
	public static final SerializableDataBuilder<Explosion.BlockInteraction> BACKWARDS_COMPATIBLE_DESTRUCTION_TYPE = SerializableDataTypes.mapped(Explosion.BlockInteraction.class,
		HashBiMap.create(ImmutableBiMap.of(
			"none", Explosion.BlockInteraction.KEEP,
			"break", Explosion.BlockInteraction.DESTROY,
			"destroy", Explosion.BlockInteraction.DESTROY_WITH_DECAY)
		));
	public static final SerializableDataBuilder<ArgumentWrapper<EntitySelector>> ENTITIES_SELECTOR = SerializableDataTypes.argumentType(EntityArgument.entities());
	public static final SerializableDataBuilder<ClickAction> CLICK_TYPE = SerializableDataTypes.enumValue(ClickAction.class);
	public static final SerializableDataBuilder<TextDisplay.TextAlignment> TEXT_ALIGNMENT = SerializableDataTypes.enumValue(TextDisplay.TextAlignment.class);
	public static final SerializableDataBuilder<Map<ResourceLocation, ResourceLocation>> IDENTIFIER_MAP = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jsonObject)) {
				throw new JsonParseException("Expected a JSON object");
			}

			Map<ResourceLocation, ResourceLocation> map = new LinkedHashMap<>();
			for (String key : jsonObject.keySet()) {

				if (!(jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
					continue;
				}

				ResourceLocation keyId = ResourceLocation.parse(key);
				ResourceLocation valId = ResourceLocation.parse(jsonPrimitive.getAsString());

				map.put(keyId, valId);

			}

			return map;
		}, Map.class
	);
	public static final SerializableDataBuilder<Map<Pattern, ResourceLocation>> REGEX_MAP = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jsonObject)) {
				throw new JsonSyntaxException("Expected a JSON object.");
			}

			Map<Pattern, ResourceLocation> regexMap = new HashMap<>();
			for (String key : jsonObject.keySet()) {

				if (!(jsonObject.get(key) instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
					continue;
				}

				Pattern pattern = Pattern.compile(key);
				ResourceLocation id = SerializableDataTypes.IDENTIFIER.deserialize(jsonPrimitive);

				regexMap.put(pattern, id);

			}

			return regexMap;
		}, Map.class
	);
	public static final SerializableDataBuilder<GameType> GAME_MODE = SerializableDataTypes.enumValue(GameType.class);
	public static final SerializableDataBuilder<Component> DEFAULT_TRANSLATABLE_TEXT = SerializableDataBuilder.of(
		(jsonElement) -> {
			return jsonElement instanceof JsonPrimitive jsonPrimitive
				? Component.translatable(jsonPrimitive.getAsString())
				: SerializableDataTypes.TEXT.deserialize(jsonElement);
		}, Comparison.class
	);
	public static final SerializableDataBuilder<Pose> ENTITY_POSE = SerializableDataTypes.enumValue(Pose.class);
	public static final SerializableDataBuilder<HudRender> HUD_RENDER = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo)) {
				throw new JsonSyntaxException("HudRender should be instanceof a JsonObject!");
			}
			return new HudRender(
				jo.has("should_render") ? SerializableDataTypes.BOOLEAN.deserialize(jo.get("should_render")) : true,
				jo.has("condition") ? ENTITY_CONDITION.deserialize(jo.get("condition")) : null
			);
		}, HudRender.class
	);

	public static <T> @NotNull SerializableDataBuilder<ActionFactory<T>> action(RegistryKey<ActionFactory<T>> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Expected a JSON object.");
				}

				ResourceLocation factoryID = SerializableDataTypes.IDENTIFIER.deserialize(jsonObject.get("type"));
				try {
					ActionFactory<T> actionFactory = CalioRegistry.INSTANCE.retrieve(registry).get(factoryID);
					if (actionFactory == null) {
						throw new IllegalArgumentException("Unable to retrieve action of: " + jsonObject.get("type").getAsString());
					}
					return actionFactory.copy().decompile(jsonObject);
				} catch (Throwable e) {
					OriginsPaper.getPlugin().getLog4JLogger().error("Unable to retrieve action of `{}` for Power!", factoryID.toString());
					throw e;
				}
			}, ActionFactory.class
		);
	}

	public static <T> @NotNull SerializableDataBuilder<ConditionFactory<T>> condition(RegistryKey<ConditionFactory<T>> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Expected a JSON object.");
				}

				ResourceLocation factoryID = SerializableDataTypes.IDENTIFIER.deserialize(jsonObject.get("type"));
				try {
					ConditionFactory<T> conditionFactory = CalioRegistry.INSTANCE.retrieve(registry).get(factoryID);
					if (conditionFactory == null) {
						throw new IllegalArgumentException("Unable to retrieve action of: " + jsonObject.get("type").getAsString());
					}
					return conditionFactory.copy().decompile(jsonObject);
				} catch (Throwable e) {
					OriginsPaper.getPlugin().getLog4JLogger().error("Unable to retrieve condition of `{}` for Power!", factoryID.toString());
					throw e;
				}
			}, ConditionFactory.class
		);
	}

	public static void init() {
	}

}
