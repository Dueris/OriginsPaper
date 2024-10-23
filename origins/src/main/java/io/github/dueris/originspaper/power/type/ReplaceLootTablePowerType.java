package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.IdentifiedLootTable;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ReplaceLootTablePowerType extends PowerType {

	public static final ResourceKey<LootTable> REPLACED_TABLE_KEY = ResourceKey.create(Registries.LOOT_TABLE, OriginsPaper.apoliIdentifier("replaced_loot_table"));
	public static ResourceLocation LAST_REPLACED_TABLE_ID;

	private static final Stack<LootTable> REPLACEMENT_STACK = new Stack<>();
	private static final Stack<LootTable> BACKTRACK_STACK = new Stack<>();

	private final Map<Pattern, ResourceLocation> replacements;

	private final int priority;

	private final Predicate<Tuple<Level, ItemStack>> itemCondition;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final Predicate<BlockInWorld> blockCondition;

	public ReplaceLootTablePowerType(Power power, LivingEntity entity, Map<Pattern, ResourceLocation> replacements, int priority, Predicate<Tuple<Level, ItemStack>> itemCondition, Predicate<Tuple<Entity, Entity>> biEntityCondition, Predicate<BlockInWorld> blockCondition) {
		super(power, entity);
		this.replacements = replacements;
		this.priority = priority;
		this.itemCondition = itemCondition;
		this.biEntityCondition = biEntityCondition;
		this.blockCondition = blockCondition;
	}

	public boolean hasReplacement(ResourceKey<LootTable> lootTableKey) {

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

	public boolean doesApply(Entity contextEntity, ItemStack toolStack, SavedBlockPosition cachedBlock) {
		return (biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, contextEntity)))
			&& (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), toolStack)))
			&& (blockCondition == null || blockCondition.test(cachedBlock));
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

	public int getPriority() {
		return priority;
	}

	public static void clearStack() {
		REPLACEMENT_STACK.clear();
		BACKTRACK_STACK.clear();
	}

	public static void addToStack(LootTable lootTable) {
		REPLACEMENT_STACK.add(lootTable);
	}

	public static LootTable pop() {

		if(REPLACEMENT_STACK.isEmpty()) {
			return LootTable.EMPTY;
		}

		LootTable table = REPLACEMENT_STACK.pop();
		BACKTRACK_STACK.push(table);

		return table;

	}

	public static LootTable restore() {

		if(BACKTRACK_STACK.isEmpty()) {
			return LootTable.EMPTY;
		}

		LootTable table = BACKTRACK_STACK.pop();
		REPLACEMENT_STACK.push(table);

		return table;

	}

	public static LootTable peek() {

		if(REPLACEMENT_STACK.isEmpty()) {
			return LootTable.EMPTY;
		}

		return REPLACEMENT_STACK.peek();

	}

	private static void printStacks() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		int count = 0;
		while(!REPLACEMENT_STACK.isEmpty()) {
			LootTable t = pop();
			stringBuilder.append(t == null ? "null" : ((IdentifiedLootTable)t).apoli$getLootTableKey());
			if(!REPLACEMENT_STACK.isEmpty()) {
				stringBuilder.append(", ");
			}
			count++;
		}
		stringBuilder.append("], [");
		while(count > 0) {
			restore();
			count--;
		}
		while(!BACKTRACK_STACK.isEmpty()) {
			LootTable t = restore();
			stringBuilder.append(t == null ? "null" : ((IdentifiedLootTable)t).apoli$getLootTableKey());
			if(!BACKTRACK_STACK.isEmpty()) {
				stringBuilder.append(", ");
			}
			count++;
		}
		while(count > 0) {
			pop();
			count--;
		}
		stringBuilder.append("]");
		OriginsPaper.LOGGER.info(stringBuilder.toString());
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("replace_loot_table"),
			new SerializableData()
				.add("replace", ApoliDataTypes.REGEX_MAP)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new ReplaceLootTablePowerType(power, entity,
				data.get("replace"),
				data.get("priority"),
				data.get("item_condition"),
				data.get("bientity_condition"),
				data.get("block_condition")
			)
		).allowCondition();
	}

}
