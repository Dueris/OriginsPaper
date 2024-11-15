package io.github.dueris.originspaper.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.*;
import io.github.dueris.calio.SerializationHelper;
import io.github.dueris.calio.data.CompoundSerializableDataType;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.calio.registry.DataObjectFactories;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.originspaper.action.AbstractAction;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.action.type.meta.AndMetaActionType;
import io.github.dueris.originspaper.condition.AbstractCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.AbstractConditionType;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.Active;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.util.*;
import io.github.dueris.originspaper.util.context.TypeActionContext;
import io.github.dueris.originspaper.util.context.TypeConditionContext;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ApoliDataTypes {

	public static final SerializableDataType<PowerReference> POWER_REFERENCE = SerializableDataTypes.IDENTIFIER.xmap(PowerReference::of, PowerReference::id);

	public static final SerializableDataType<PowerReference> RESOURCE_REFERENCE = SerializableDataTypes.IDENTIFIER.xmap(PowerReference::resource, PowerReference::id);

	public static final SerializableDataType<Space> SPACE = SerializableDataType.enumValue(Space.class);

	public static final SerializableDataType<ResourceOperation> RESOURCE_OPERATION = SerializableDataType.enumValue(ResourceOperation.class);

	public static final SerializableDataType<InventoryUtil.InventoryType> INVENTORY_TYPE = SerializableDataType.enumValue(InventoryUtil.InventoryType.class);

	public static final SerializableDataType<EnumSet<InventoryUtil.InventoryType>> INVENTORY_TYPE_SET = SerializableDataType.enumSet(INVENTORY_TYPE);

	public static final SerializableDataType<InventoryUtil.ProcessMode> PROCESS_MODE = SerializableDataType.enumValue(InventoryUtil.ProcessMode.class);

	public static final SerializableDataType<AttributedEntityAttributeModifier> ATTRIBUTED_ATTRIBUTE_MODIFIER = SerializableDataType.compound(
		DataObjectFactories.ATTRIBUTE_MODIFIER.getSerializableData().copy()
			.add("attribute", SerializableDataTypes.ATTRIBUTE_ENTRY),
		data -> new AttributedEntityAttributeModifier(
			data.get("attribute"),
			DataObjectFactories.ATTRIBUTE_MODIFIER.fromData(data)
		),
		(attributedAttributeModifier, serializableData) -> DataObjectFactories.ATTRIBUTE_MODIFIER
			.toData(attributedAttributeModifier.modifier(), serializableData)
			.set("attribute", attributedAttributeModifier.attribute())
	);

	public static final SerializableDataType<List<AttributedEntityAttributeModifier>> ATTRIBUTED_ATTRIBUTE_MODIFIERS = ATTRIBUTED_ATTRIBUTE_MODIFIER.list(1, Integer.MAX_VALUE);

	@Deprecated(forRemoval = true)
	public static final SerializableDataType<Tuple<Integer, ItemStack>> POSITIONED_ITEM_STACK = SerializableDataType.compound(
		DataObjectFactories.ITEM_STACK.getSerializableData().copy()
			.add("slot", SerializableDataTypes.INT, Integer.MIN_VALUE),
		data -> new Tuple<>(
			data.getInt("slot"),
			DataObjectFactories.ITEM_STACK.fromData(data)
		),
		(positionedStack, serializableData) -> DataObjectFactories.ITEM_STACK
			.toData(positionedStack.getB(), serializableData)
			.set("slot", positionedStack.getA())
	);

	@Deprecated(forRemoval = true)
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

	public static final SerializableDataType<Comparison> COMPARISON = SerializableDataType.enumValue(Comparison.class, SerializationHelper.buildEnumMap(Comparison.class, Comparison::getComparisonString));

	public static final SerializableDataType<ArgumentWrapper<Integer>> ITEM_SLOT = SerializableDataType.argumentType(SlotArgument.slot());

	public static final SerializableDataType<List<ArgumentWrapper<Integer>>> ITEM_SLOTS = ITEM_SLOT.list();

	public static final SerializableDataType<SlotRange> SLOT_RANGE = SerializableDataType.of(SlotRangesUtil.INDEX_OR_STRING_CODEC);

	public static final SerializableDataType<List<SlotRange>> SLOT_RANGES = SLOT_RANGE.list();

	public static final SerializableDataType<SlotRange> SINGLE_SLOT_RANGE = SerializableDataType.of(SlotRangesUtil.SINGLE_INDEX_OR_STRING_CODEC);

	public static final SerializableDataType<List<SlotRange>> SINGLE_SLOT_RANGES = SINGLE_SLOT_RANGE.list();

	public static final SerializableDataType<Explosion.BlockInteraction> DESTRUCTION_TYPE = SerializableDataType.enumValue(Explosion.BlockInteraction.class);

	public static final SerializableDataType<ArgumentWrapper<EntitySelector>> ENTITIES_SELECTOR = SerializableDataType.argumentType(EntityArgument.entities());

	public static final SerializableDataType<AdvancementCommands.Action> ADVANCEMENT_OPERATION = SerializableDataType.enumValue(AdvancementCommands.Action.class);

	public static final SerializableDataType<AdvancementCommands.Mode> ADVANCEMENT_SELECTION = SerializableDataType.enumValue(AdvancementCommands.Mode.class);

	public static final SerializableDataType<ClickAction> CLICK_TYPE = SerializableDataType.enumValue(ClickAction.class, () -> ImmutableMap.of(
		"left", ClickAction.PRIMARY,
		"right", ClickAction.SECONDARY
	));

	public static final SerializableDataType<EnumSet<ClickAction>> CLICK_TYPE_SET = SerializableDataType.enumSet(CLICK_TYPE);

	public static final SerializableDataType<TextAlignment> TEXT_ALIGNMENT = SerializableDataType.enumValue(TextAlignment.class);

	public static final SerializableDataType<Map<ResourceLocation, ResourceLocation>> IDENTIFIER_MAP = SerializableDataType.map(SerializableDataTypes.IDENTIFIER, SerializableDataTypes.IDENTIFIER);

	public static final SerializableDataType<Pattern> REGEX = SerializableDataTypes.STRING.xmap(Pattern::compile, Pattern::pattern);

	public static final SerializableDataType<Map<Pattern, ResourceLocation>> REGEX_MAP = SerializableDataType.map(REGEX, SerializableDataTypes.IDENTIFIER);

	public static final SerializableDataType<GameType> GAME_MODE = SerializableDataType.enumValue(GameType.class);

	//  This is for keeping backwards compatibility to fields that used to accept strings as translation keys
	public static final SerializableDataType<Component> DEFAULT_TRANSLATABLE_TEXT = SerializableDataType.of(
		new Codec<>() {

			@Override
			public <T> DataResult<com.mojang.datafixers.util.Pair<Component, T>> decode(DynamicOps<T> ops, T input) {

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
	public static final SerializableDataType<Float> NORMALIZED_FLOAT = SerializableDataType.boundNumber(SerializableDataTypes.FLOAT, 0F, 1F);
	public static final SerializableDataType<ContainerType> CONTAINER_TYPE = SerializableDataType.registry(ApoliRegistries.CONTAINER_TYPE, "apoli", true);
	public static SerializableDataType<CraftingRecipe> DISALLOWING_INTERNAL_CRAFTING_RECIPE = SerializableDataTypes.RECIPE.comapFlatMap(RecipeUtil::validateCraftingRecipe, Function.identity());

	@SuppressWarnings("unchecked")
	public static <T extends TypeConditionContext, C extends AbstractCondition<T, CT>, CT extends AbstractConditionType<T, C>> CompoundSerializableDataType<C> condition(String typeField, SerializableDataType<ConditionConfiguration<CT>> registryDataType, BiFunction<CT, Boolean, C> constructor) {
		return new CompoundSerializableDataType<>(
			new SerializableData()
				.add(typeField, registryDataType)
				.add("inverted", SerializableDataTypes.BOOLEAN, false),
			serializableData -> {
				boolean root = serializableData.isRoot();
				return new MapCodec<>() {

					@Override
					public <I> Stream<I> keys(DynamicOps<I> ops) {
						return serializableData.keys(ops);
					}

					@Override
					public <I> DataResult<C> decode(DynamicOps<I> ops, MapLike<I> input) {

						DataResult<SerializableData.Instance> conditionDataResult = serializableData.decode(ops, input);
						DataResult<CT> conditionTypeResult = conditionDataResult
							.map(conditionData -> (ConditionConfiguration<CT>) conditionData.get(typeField))
							.flatMap(config -> config.mapCodec(root).decode(ops, input));

						return conditionDataResult
							.flatMap(conditionData -> conditionTypeResult
								.map(conditionType -> constructor.apply(conditionType, conditionData.getBoolean("inverted"))));

					}

					@Override
					public <I> RecordBuilder<I> encode(C input, DynamicOps<I> ops, RecordBuilder<I> prefix) {

						CT conditionType = input.getConditionType();
						ConditionConfiguration<CT> config = (ConditionConfiguration<CT>) conditionType.getConfig();

						prefix.add(typeField, registryDataType.write(ops, config));
						config.mapCodec(root).encode(conditionType, ops, prefix);

						prefix.add("inverted", ops.createBoolean(input.isInverted()));
						return prefix;

					}

				};
			}
		);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TypeActionContext<?>, A extends AbstractAction<T, AT>, AT extends AbstractActionType<T, A>> CompoundSerializableDataType<A> action(String typeField, SerializableDataType<ActionConfiguration<AT>> registryDataType, Function<AT, A> constructor) {
		return new CompoundSerializableDataType<>(
			new SerializableData()
				.add(typeField, registryDataType),
			serializableData -> {
				boolean root = serializableData.isRoot();
				return new MapCodec<>() {

					@Override
					public <I> Stream<I> keys(DynamicOps<I> ops) {
						return serializableData.keys(ops);
					}

					@Override
					public <I> DataResult<A> decode(DynamicOps<I> ops, MapLike<I> input) {
						return serializableData.decode(ops, input)
							.map(actionData -> (ActionConfiguration<AT>) actionData.get(typeField))
							.flatMap(config -> config.mapCodec(root).decode(ops, input)
								.map(constructor));
					}

					@Override
					public <I> RecordBuilder<I> encode(A input, DynamicOps<I> ops, RecordBuilder<I> prefix) {

						AT actionType = input.getActionType();
						ActionConfiguration<AT> config = (ActionConfiguration<AT>) actionType.getConfig();

						prefix.add(typeField, registryDataType.write(ops, config));
						config.mapCodec(root).encode(actionType, ops, prefix);

						return prefix;

					}

				};
			}
		);
	}

	@SuppressWarnings("unchecked")
	public static <T extends TypeActionContext<?>, A extends AbstractAction<T, AT>, AT extends AbstractActionType<T, A>, M extends AbstractActionType<T, A> & AndMetaActionType<T, A>> SerializableDataType<A> actions(String typeField, SerializableDataType<ActionConfiguration<AT>> registryDataType, Function<List<A>, M> multiActionsConstructor, Function<AT, A> constructor) {

		CompoundSerializableDataType<A> dataType = action(typeField, registryDataType, constructor);
		SerializableDataType<List<A>> listDataType = dataType.list();

		return SerializableDataType.recursive(self -> SerializableDataType.of(
			new Codec<>() {

				@Override
				public <I> DataResult<com.mojang.datafixers.util.Pair<A, I>> decode(DynamicOps<I> ops, I input) {

					if (ops.getList(input).isSuccess()) {
						return listDataType.setRoot(self.isRoot()).codec().decode(ops, input)
							.map(actionsAndInput -> actionsAndInput
								.mapFirst(multiActionsConstructor)
								.mapFirst(m -> constructor.apply((AT) m)));
					} else {
						return dataType.setRoot(self.isRoot()).codec().decode(ops, input);
					}

				}

				@Override
				public <I> DataResult<I> encode(A input, DynamicOps<I> ops, I prefix) {
					return dataType.setRoot(self.isRoot()).codec().encode(input, ops, prefix);
				}

			}
		));

	}

}
