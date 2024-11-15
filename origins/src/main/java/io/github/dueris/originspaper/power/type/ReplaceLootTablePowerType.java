package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Pattern;

public class ReplaceLootTablePowerType extends PowerType implements Prioritized<ReplaceLootTablePowerType> {

	public static final ResourceKey<LootTable> REPLACED_TABLE_KEY = ResourceKey.create(Registries.LOOT_TABLE, OriginsPaper.apoliIdentifier("replaced_loot_table"));
	public static final TypedDataObjectFactory<ReplaceLootTablePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("replace", ApoliDataTypes.REGEX_MAP, null)
			.addFunctionedDefault("replacements", ApoliDataTypes.REGEX_MAP, data -> data.get("replace"))
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("priority", SerializableDataTypes.INT, 0)
			.validate(Util.validateAnyFieldsPresent("replace", "replacements")),
		(data, condition) -> new ReplaceLootTablePowerType(
			data.get("replacements"),
			data.get("bientity_condition"),
			data.get("block_condition"),
			data.get("item_condition"),
			data.get("priority"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("replacements", powerType.replacements)
			.set("bientity_condition", powerType.biEntityCondition)
			.set("block_condition", powerType.blockCondition)
			.set("item_condition", powerType.itemCondition)
			.set("priority", powerType.getPriority())
	);
	private static final Stack<LootTable> REPLACEMENT_STACK = new Stack<>();
	private static final Stack<LootTable> BACKTRACK_STACK = new Stack<>();
	public static ResourceLocation LAST_REPLACED_TABLE_ID;
	private final Map<Pattern, ResourceLocation> replacements;
	private final Optional<BiEntityCondition> biEntityCondition;

	private final Optional<BlockCondition> blockCondition;
	private final Optional<ItemCondition> itemCondition;

	private final int priority;

	public ReplaceLootTablePowerType(Map<Pattern, ResourceLocation> replacements, Optional<BiEntityCondition> biEntityCondition, Optional<BlockCondition> blockCondition, Optional<ItemCondition> itemCondition, int priority, Optional<EntityCondition> condition) {
		super(condition);
		this.replacements = replacements;
		this.biEntityCondition = biEntityCondition;
		this.blockCondition = blockCondition;
		this.itemCondition = itemCondition;
		this.priority = priority;
	}

	public static void clearStack() {
		REPLACEMENT_STACK.clear();
		BACKTRACK_STACK.clear();
	}

	public static void addToStack(LootTable lootTable) {
		REPLACEMENT_STACK.add(lootTable);
	}

	public static LootTable pop() {

		if (REPLACEMENT_STACK.isEmpty()) {
			return LootTable.EMPTY;
		}

		LootTable table = REPLACEMENT_STACK.pop();
		BACKTRACK_STACK.push(table);

		return table;

	}

	public static LootTable restore() {

		if (BACKTRACK_STACK.isEmpty()) {
			return LootTable.EMPTY;
		}

		LootTable table = BACKTRACK_STACK.pop();
		REPLACEMENT_STACK.push(table);

		return table;

	}

	public static LootTable peek() {

		if (REPLACEMENT_STACK.isEmpty()) {
			return LootTable.EMPTY;
		} else {
			return REPLACEMENT_STACK.peek();
		}

	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.REPLACE_LOOT_TABLE;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean hasReplacement(@NotNull ResourceKey<LootTable> lootTableKey) {

		String id = lootTableKey.location().toString();

		return replacements.keySet()
			.stream()
			.anyMatch(regex -> regex.pattern().equals(id) || regex.matcher(id).matches());

	}

	public boolean doesApply(@NotNull LootContext context) {

		Entity contextEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		ItemStack toolStack = context.hasParam(LootContextParams.TOOL) ? context.getParamOrNull(LootContextParams.TOOL) : ItemStack.EMPTY;
		SavedBlockPosition savedBlockPosition = SavedBlockPosition.fromLootContext(context);

		return doesApply(contextEntity, toolStack, savedBlockPosition);

	}

	public boolean doesApply(Entity contextEntity, ItemStack toolStack, SavedBlockPosition savedBlock) {
		return itemCondition.map(condition -> condition.test(getHolder().level(), toolStack)).orElse(true)
			&& blockCondition.map(condition -> condition.test(savedBlock)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(getHolder(), contextEntity)).orElse(true);
	}

	public ResourceKey<LootTable> getReplacement(@NotNull ResourceKey<LootTable> lootTableKey) {
		String lootTableId = lootTableKey.location().toString();
		return replacements.entrySet()
			.stream()
			.filter(entry -> entry.getKey().pattern().equals(lootTableId) || entry.getKey().matcher(lootTableId).matches())
			.findFirst()
			.map(entry -> ResourceKey.create(Registries.LOOT_TABLE, entry.getValue()))
			.orElseThrow();
	}

}
