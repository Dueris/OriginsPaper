package io.github.dueris.originspaper.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.BlockActionTypes;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.action.type.ItemActionTypes;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.*;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.power.type.Active;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.util.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ApoliDataTypes {

	public static final SerializableDataType<PowerReference> POWER_REFERENCE = SerializableDataTypes.IDENTIFIER.xmap(PowerReference::of, Power::getId);

	public static final SerializableDataType<PowerReference> RESOURCE_REFERENCE = SerializableDataTypes.IDENTIFIER.xmap(PowerReference::resource, Power::getId);

	public static final SerializableDataType<PowerTypeFactory<? extends PowerType>> POWER_TYPE_FACTORY = SerializableDataType.lazy(() -> SerializableDataType.registry(ApoliRegistries.POWER_FACTORY, "apoli", PowerTypes.ALIASES, (registry, id) -> "Power type \"" + id + "\" is not registered"));

	public static final SerializableDataType<ConditionTypeFactory<Entity>.Instance> ENTITY_CONDITION = condition(ApoliRegistries.ENTITY_CONDITION, EntityConditionTypes.ALIASES, "Entity condition type");

	public static final SerializableDataType<List<ConditionTypeFactory<Entity>.Instance>> ENTITY_CONDITIONS = ENTITY_CONDITION.list();

	public static final SerializableDataType<ConditionTypeFactory<Tuple<Entity, Entity>>.Instance> BIENTITY_CONDITION = condition(ApoliRegistries.BIENTITY_CONDITION, BiEntityConditionTypes.ALIASES, "Bi-entity condition type");

	public static final SerializableDataType<List<ConditionTypeFactory<Tuple<Entity, Entity>>.Instance>> BIENTITY_CONDITIONS = BIENTITY_CONDITION.list();

	public static final SerializableDataType<ConditionTypeFactory<Tuple<Level, ItemStack>>.Instance> ITEM_CONDITION = condition(ApoliRegistries.ITEM_CONDITION, ItemConditionTypes.ALIASES, "Item condition type");

	public static final SerializableDataType<List<ConditionTypeFactory<Tuple<Level, ItemStack>>.Instance>> ITEM_CONDITIONS = ITEM_CONDITION.list();

	public static final SerializableDataType<ConditionTypeFactory<BlockInWorld>.Instance> BLOCK_CONDITION = condition(ApoliRegistries.BLOCK_CONDITION, BlockConditionTypes.ALIASES, "Block condition type");

	public static final SerializableDataType<List<ConditionTypeFactory<BlockInWorld>.Instance>> BLOCK_CONDITIONS = BLOCK_CONDITION.list();

	public static final SerializableDataType<ConditionTypeFactory<FluidState>.Instance> FLUID_CONDITION = condition(ApoliRegistries.FLUID_CONDITION, FluidConditionTypes.ALIASES, "Fluid condition type");

	public static final SerializableDataType<List<ConditionTypeFactory<FluidState>.Instance>> FLUID_CONDITIONS = FLUID_CONDITION.list();

	public static final SerializableDataType<ConditionTypeFactory<Tuple<DamageSource, Float>>.Instance> DAMAGE_CONDITION = SerializableDataType.lazy(() -> condition(ApoliRegistries.DAMAGE_CONDITION, DamageConditionTypes.ALIASES, "Damage condition type"));

	public static final SerializableDataType<List<ConditionTypeFactory<Tuple<DamageSource, Float>>.Instance>> DAMAGE_CONDITIONS = SerializableDataType.lazy(DAMAGE_CONDITION::list);

	public static final SerializableDataType<ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>>.Instance> BIOME_CONDITION = condition(ApoliRegistries.BIOME_CONDITION, BiomeConditionTypes.ALIASES, "Biome condition type");

	public static final SerializableDataType<List<ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>>.Instance>> BIOME_CONDITIONS = BIOME_CONDITION.list();

	public static final SerializableDataType<ActionTypeFactory<Entity>.Instance> ENTITY_ACTION = action(ApoliRegistries.ENTITY_ACTION, EntityActionTypes.ALIASES, "Entity action type");

	public static final SerializableDataType<List<ActionTypeFactory<Entity>.Instance>> ENTITY_ACTIONS = ENTITY_ACTION.list();

	public static final SerializableDataType<ActionTypeFactory<Tuple<Entity, Entity>>.Instance> BIENTITY_ACTION = action(ApoliRegistries.BIENTITY_ACTION, BiEntityActionTypes.ALIASES, "Bi-entity action type");

	public static final SerializableDataType<List<ActionTypeFactory<Tuple<Entity, Entity>>.Instance>> BIENTITY_ACTIONS = BIENTITY_ACTION.list();

	public static final SerializableDataType<ActionTypeFactory<Triple<Level, BlockPos, Direction>>.Instance> BLOCK_ACTION = action(ApoliRegistries.BLOCK_ACTION, BlockActionTypes.ALIASES, "Block action type");

	public static final SerializableDataType<List<ActionTypeFactory<Triple<Level, BlockPos, Direction>>.Instance>> BLOCK_ACTIONS = BLOCK_ACTION.list();

	public static final SerializableDataType<ActionTypeFactory<Tuple<Level, SlotAccess>>.Instance> ITEM_ACTION = action(ApoliRegistries.ITEM_ACTION, ItemActionTypes.ALIASES, "Item action type");

	public static final SerializableDataType<List<ActionTypeFactory<Tuple<Level, SlotAccess>>.Instance>> ITEM_ACTIONS = ITEM_ACTION.list();

	public static final SerializableDataType<Space> SPACE = SerializableDataType.enumValue(Space.class);

	public static final SerializableDataType<ResourceOperation> RESOURCE_OPERATION = SerializableDataType.enumValue(ResourceOperation.class);

	public static final SerializableDataType<InventoryUtil.InventoryType> INVENTORY_TYPE = SerializableDataType.enumValue(InventoryUtil.InventoryType.class);

	public static final SerializableDataType<EnumSet<InventoryUtil.InventoryType>> INVENTORY_TYPE_SET = SerializableDataType.enumSet(INVENTORY_TYPE);

	public static final SerializableDataType<InventoryUtil.ProcessMode> PROCESS_MODE = SerializableDataType.enumValue(InventoryUtil.ProcessMode.class);

	public static final SerializableDataType<AttributedEntityAttributeModifier> ATTRIBUTED_ATTRIBUTE_MODIFIER = SerializableDataType.compound(
		SerializableDataTypes.ATTRIBUTE_MODIFIER.serializableData().copy()
			.add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY),
		data -> new AttributedEntityAttributeModifier(
			data.get("attribute"),
			SerializableDataTypes.ATTRIBUTE_MODIFIER_OBJ_FACTORY.fromData(data)
		),
		(attributedAttributeModifier, serializableData) -> SerializableDataTypes.ATTRIBUTE_MODIFIER_OBJ_FACTORY
			.toData(attributedAttributeModifier.modifier(), serializableData)
			.set("attribute", attributedAttributeModifier.attribute())
	);

	public static final SerializableDataType<List<AttributedEntityAttributeModifier>> ATTRIBUTED_ATTRIBUTE_MODIFIERS = ATTRIBUTED_ATTRIBUTE_MODIFIER.list();

	public static final SerializableDataType<Tuple<Integer, ItemStack>> POSITIONED_ITEM_STACK = SerializableDataType.compound(
		SerializableDataTypes.ITEM_STACK.serializableData().copy()
			.add("slot", SerializableDataTypes.INT, Integer.MIN_VALUE),
		data -> new Tuple<>(
			data.getInt("slot"),
			SerializableDataTypes.ITEM_STACK_OBJ_FACTORY.fromData(data)
		),
		(positionedStack, serializableData) -> SerializableDataTypes.ITEM_STACK_OBJ_FACTORY
			.toData(positionedStack.getB(), serializableData)
			.set("slot", positionedStack.getA())
	);

	public static final SerializableDataType<List<Tuple<Integer, ItemStack>>> POSITIONED_ITEM_STACKS = POSITIONED_ITEM_STACK.list();

	public static final SerializableDataType<Active.Key> KEY = SerializableDataType.compound(
		new SerializableData()
			.add("key", SerializableDataTypes.STRING)
			.add("continuous", SerializableDataTypes.BOOLEAN, false),
		data -> {

			Active.Key key = new Active.Key();

			key.key = data.getString("key");
			key.continuous = data.getBoolean("continuous");

			return key;

		},
		(key, serializableData) -> serializableData.instance()
			.set("key", key.key)
			.set("continuous", key.continuous)
	);

	public static final SerializableDataType<Active.Key> BACKWARDS_COMPATIBLE_KEY = SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<com.mojang.datafixers.util.Pair<Active.Key, T>> decode(DynamicOps<T> ops, T input) {

				DataResult<String> stringInput = ops.getStringValue(input);
				if (stringInput.isSuccess()) {

					Active.Key key = new Active.Key();

					key.key = stringInput.getOrThrow();
					key.continuous = false;

					return DataResult.success(com.mojang.datafixers.util.Pair.of(key, input));

				} else {
					return KEY.codec().decode(ops, input);
				}

			}

			@Override
			public <T> DataResult<T> encode(Active.Key input, DynamicOps<T> ops, T prefix) {
				return KEY.codec().encode(input, ops, prefix);
			}

		}
	);

	/**
	 * <p>A HUD render data type that accepts either a single HUD render or multiple HUD renders. The first HUD render will be considered
	 * the <b>"parent"</b> and the following HUD renders will be considered its <b>"children."</b></p>
	 *
	 * <p>If the children don't specify an order value, the order value of the parent will be inherited instead.</p>
	 */
	public static final SerializableDataType<HudRender> HUD_RENDER = HudRender.DATA_TYPE;

	public static final SerializableDataType<Comparison> COMPARISON = SerializableDataType.enumValue(Comparison.class, io.github.dueris.calio.util.Util.buildEnumMap(Comparison.class, Comparison::getComparisonString));

	public static final SerializableDataType<ArgumentWrapper<Integer>> ITEM_SLOT = SerializableDataType.argumentType(SlotArgument.slot());

	public static final SerializableDataType<List<ArgumentWrapper<Integer>>> ITEM_SLOTS = ITEM_SLOT.list();

	public static final SerializableDataType<Explosion.BlockInteraction> DESTRUCTION_TYPE = SerializableDataType.enumValue(Explosion.BlockInteraction.class);

	public static final SerializableDataType<ArgumentWrapper<EntitySelector>> ENTITIES_SELECTOR = SerializableDataType.argumentType(EntityArgument.entities());

	public static final SerializableDataType<ClickAction> CLICK_TYPE = SerializableDataType.enumValue(ClickAction.class, () -> ImmutableMap.of(
		"left", ClickAction.PRIMARY,
		"right", ClickAction.SECONDARY
	));

	public static final SerializableDataType<EnumSet<ClickAction>> CLICK_TYPE_SET = SerializableDataType.enumSet(CLICK_TYPE);

	public static final SerializableDataType<TextDisplay.TextAlignment> TEXT_ALIGNMENT = SerializableDataType.enumValue(TextDisplay.TextAlignment.class);

	public static final SerializableDataType<Map<ResourceLocation, ResourceLocation>> IDENTIFIER_MAP = SerializableDataType.map(SerializableDataTypes.IDENTIFIER, SerializableDataTypes.IDENTIFIER);

	public static final SerializableDataType<Pattern> REGEX = SerializableDataTypes.STRING.xmap(Pattern::compile, Pattern::pattern);

	public static final SerializableDataType<Map<Pattern, ResourceLocation>> REGEX_MAP = SerializableDataType.map(REGEX, SerializableDataTypes.IDENTIFIER);

	public static final SerializableDataType<GameType> GAME_MODE = SerializableDataType.enumValue(GameType.class);

	public static final SerializableDataType<Component> DEFAULT_TRANSLATABLE_TEXT = SerializableDataType.of(
		new Codec<>() {
			@Override
			public <T> DataResult<Pair<Component, T>> decode(DynamicOps<T> ops, T input) {
				DataResult<String> inputString = ops.getStringValue(input);
				if (inputString.isSuccess()) {
					return inputString
						.map(Component::translatable)
						.map(text -> com.mojang.datafixers.util.Pair.of(text, input));
				} else {
					return SerializableDataTypes.TEXT.codec().decode(ops, input);
				}
			}

			@Override
			public <T> DataResult<T> encode(Component input, DynamicOps<T> ops, T prefix) {
				return SerializableDataTypes.TEXT.codec().encode(input, ops, prefix);
			}
		}
	);

	public static final SerializableDataType<StackClickPhase> STACK_CLICK_PHASE = SerializableDataType.enumValue(StackClickPhase.class);

	public static final SerializableDataType<EnumSet<StackClickPhase>> STACK_CLICK_PHASE_SET = SerializableDataType.enumSet(STACK_CLICK_PHASE);

	public static final SerializableDataType<BlockUsagePhase> BLOCK_USAGE_PHASE = SerializableDataType.enumValue(BlockUsagePhase.class);

	public static final SerializableDataType<EnumSet<BlockUsagePhase>> BLOCK_USAGE_PHASE_SET = SerializableDataType.enumSet(BLOCK_USAGE_PHASE);

	public static final SerializableDataType<Pose> ENTITY_POSE = SerializableDataType.enumValue(Pose.class);

	public static SerializableDataType<CraftingRecipe> DISALLOWING_INTERNAL_CRAFTING_RECIPE = SerializableDataTypes.RECIPE.comapFlatMap(Util::validateCraftingRecipe, Function.identity());

	public static <T> SerializableDataType<ConditionTypeFactory<T>.Instance> condition(Registry<ConditionTypeFactory<T>> registry, String name) {
		return condition(registry, null, name);
	}

	public static <T> SerializableDataType<ConditionTypeFactory<T>.Instance> condition(Registry<ConditionTypeFactory<T>> registry, IdentifierAlias aliases, String name) {
		return condition("type", registry, aliases, (conditionFactories, id) -> name + " \"" + id + "\" is not registered!");
	}

	@SuppressWarnings("unchecked")
	public static <E> SerializableDataType<ConditionTypeFactory<E>.Instance> condition(String fieldName, Registry<ConditionTypeFactory<E>> registry, @Nullable IdentifierAlias aliases, BiFunction<Registry<ConditionTypeFactory<E>>, ResourceLocation, String> errorHandler) {
		return new CompoundSerializableDataType<>(
			new SerializableData()
				.add(fieldName, SerializableDataType.registry(registry, "apoli", aliases, errorHandler)),
			serializableData -> {
				boolean root = serializableData.isRoot();
				return new MapCodec<>() {

					@Override
					public <T> Stream<T> keys(DynamicOps<T> ops) {
						return serializableData.keys(ops);
					}

					@Override
					public <T> DataResult<ConditionTypeFactory<E>.Instance> decode(DynamicOps<T> ops, MapLike<T> input) {
						return serializableData.decode(ops, input)
							.map(factoryData -> (ConditionTypeFactory<E>) factoryData.get(fieldName))
							.flatMap(factory -> factory.getSerializableData().setRoot(root).decode(ops, input)
								.map(factory::fromData));
					}

					@Override
					public <T> RecordBuilder<T> encode(ConditionTypeFactory<E>.Instance input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

						prefix.add(fieldName, SerializableDataTypes.IDENTIFIER.write(ops, input.getSerializerId()));
						input.getSerializableData().setRoot(root).encode(input.getData(), ops, prefix);

						return prefix;

					}

				};
			}
		);
	}

	public static <E> SerializableDataType<ActionTypeFactory<E>.Instance> action(Registry<ActionTypeFactory<E>> registry, String name) {
		return action(registry, null, name);
	}

	public static <E> SerializableDataType<ActionTypeFactory<E>.Instance> action(Registry<ActionTypeFactory<E>> registry, IdentifierAlias aliases, String name) {
		return action("type", registry, aliases, (factories, id) -> name + " \"" + id + "\" is not registered!");
	}

	@SuppressWarnings("unchecked")
	public static <E> SerializableDataType<ActionTypeFactory<E>.Instance> action(String fieldName, Registry<ActionTypeFactory<E>> registry, @Nullable IdentifierAlias aliases, BiFunction<Registry<ActionTypeFactory<E>>, ResourceLocation, String> errorHandler) {

		CompoundSerializableDataType<ActionTypeFactory<E>.Instance> dataType = new CompoundSerializableDataType<>(
			new SerializableData()
				.add(fieldName, SerializableDataType.registry(registry, "apoli", aliases, errorHandler)),
			serializableData -> {
				boolean root = serializableData.isRoot();
				return new MapCodec<>() {

					@Override
					public <T> Stream<T> keys(DynamicOps<T> ops) {
						return serializableData.keys(ops);
					}

					@Override
					public <T> DataResult<ActionTypeFactory<E>.Instance> decode(DynamicOps<T> ops, MapLike<T> input) {
						return serializableData.decode(ops, input)
							.map(factoryData -> (ActionTypeFactory<E>) factoryData.get(fieldName))
							.flatMap(factory -> factory.getSerializableData().setRoot(root).decode(ops, input)
								.map(factory::fromData));
					}

					@Override
					public <T> RecordBuilder<T> encode(ActionTypeFactory<E>.Instance input, DynamicOps<T> ops, RecordBuilder<T> prefix) {

						prefix.add(fieldName, SerializableDataTypes.IDENTIFIER.write(ops, input.getSerializerId()));
						input.getSerializableData().setRoot(root).encode(input.getData(), ops, prefix);

						return prefix;

					}

				};
			}
		);

		return SerializableDataType.recursive(self -> {

			SerializableDataType<ActionTypeFactory<E>.Instance> singleDataType = dataType.setRoot(self.isRoot());
			SerializableDataType<List<ActionTypeFactory<E>.Instance>> listDataType = singleDataType.list();

			return SerializableDataType.of(
				new Codec<>() {

					@Override
					public <T> DataResult<com.mojang.datafixers.util.Pair<ActionTypeFactory<E>.Instance, T>> decode(DynamicOps<T> ops, T input) {

						Optional<ActionTypeFactory<E>> optAndFactory = registry.getOptional(OriginsPaper.apoliIdentifier("and"));

						if (ops.getList(input).isSuccess() && optAndFactory.isPresent()) {
							ActionTypeFactory<E> andFactory = optAndFactory.get();
							return listDataType.codec().decode(ops, input)
								.map(actionsAndInput -> actionsAndInput
									.mapFirst(actions -> andFactory.fromData(andFactory.getSerializableData().instance()
										.set("actions", actions))));
						} else {
							return singleDataType.codec().decode(ops, input);
						}

					}

					@Override
					public <T> DataResult<T> encode(ActionTypeFactory<E>.Instance input, DynamicOps<T> ops, T prefix) {
						return singleDataType.codec().encode(input, ops, prefix);
					}

				}
			);

		});

	}

}
