package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ReplaceLootTablePower extends PowerType {
	private final Map<Pattern, ResourceLocation> replacements;

	private final int priority;

	private final Predicate<Tuple<Level, ItemStack>> itemCondition;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final Predicate<BlockInWorld> blockCondition;

	public ReplaceLootTablePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								 @NotNull Map<Pattern, ResourceLocation> replace, ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition, ConditionTypeFactory<BlockInWorld> blockCondition, ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition, int priority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.priority = priority;
		this.itemCondition = itemCondition;
		this.biEntityCondition = bientityCondition;
		this.blockCondition = blockCondition;

		this.replacements = replace;

		System.out.println(replace);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("replace_loot_table"), PowerType.getFactory().getSerializableData()
			.add("replace", ApoliDataTypes.REGEX_MAP)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("priority", SerializableDataTypes.INT, 0));
	}

	public boolean shouldReplace(@NotNull ResourceKey<LootTable> lootTableKey) {

		String id = lootTableKey.location().toString();

		return replacements.keySet()
			.stream()
			.anyMatch(regex -> regex.pattern().equals(id) || regex.matcher(id).matches());

	}

	public boolean doesApply(@NotNull LootContext context, Entity entity) {

		Entity contextEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		ItemStack toolStack = context.hasParam(LootContextParams.TOOL) ? context.getParamOrNull(LootContextParams.TOOL) : ItemStack.EMPTY;
		SavedBlockPosition savedBlockPosition = SavedBlockPosition.fromLootContext(context);

		return doesApply(contextEntity, toolStack, savedBlockPosition, entity);

	}

	public boolean doesApply(Entity contextEntity, ItemStack toolStack, SavedBlockPosition cachedBlock, Entity entity) {
		return isActive(entity) && ((biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, contextEntity)))
			&& (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), toolStack)))
			&& (blockCondition == null || blockCondition.test(cachedBlock)));
	}

	public ResourceKey<LootTable> getReplacement(@NotNull ResourceKey<LootTable> lootTableKey) throws Exception {
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

}
