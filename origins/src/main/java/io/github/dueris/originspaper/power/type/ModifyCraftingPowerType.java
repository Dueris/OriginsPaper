package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyCraftingPowerType extends ValueModifyingPowerType implements Prioritized<ModifyCraftingPowerType> {

	private final ResourceLocation recipeIdentifier;
	private final Predicate<Tuple<Level, ItemStack>> itemCondition;

	private final ItemStack newStack;
	private final Consumer<Tuple<Level, SlotAccess>> itemAction;
	private final Consumer<Tuple<Level, SlotAccess>> itemActionAfterCrafting;
	private final Consumer<Entity> entityAction;
	private final Consumer<Triple<Level, BlockPos, Direction>> blockAction;

	private final int priority;

	public ModifyCraftingPowerType(Power power, LivingEntity entity, ResourceLocation recipeIdentifier, Predicate<Tuple<Level, ItemStack>> itemCondition, ItemStack newStack, Consumer<Tuple<Level, SlotAccess>> itemAction, Consumer<Tuple<Level, SlotAccess>> itemActionAfterCrafting, Consumer<Entity> entityAction, Consumer<Triple<Level, BlockPos, Direction>> blockAction, int priority) {
		super(power, entity);
		this.recipeIdentifier = recipeIdentifier;
		this.itemCondition = itemCondition;
		this.newStack = newStack;
		this.itemAction = itemAction;
		this.itemActionAfterCrafting = itemActionAfterCrafting;
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.priority = priority;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_crafting"),
			new SerializableData()
				.add("recipe", SerializableDataTypes.IDENTIFIER, null)
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("result", SerializableDataTypes.ITEM_STACK, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("item_action_after_crafting", ApoliDataTypes.ITEM_ACTION, null)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("block_action", ApoliDataTypes.BLOCK_ACTION, null)
				.add("priority", SerializableDataTypes.INT, 0),
			data -> (power, entity) -> new ModifyCraftingPowerType(
				power,
				entity,
				data.getId("recipe"),
				data.get("item_condition"),
				data.get("result"),
				data.get("item_action"),
				data.get("item_action_after_crafting"),
				data.get("entity_action"),
				data.get("block_action"),
				data.get("priority")
			)
		).allowCondition();
	}

	public ItemStack getNewStack() {
		return newStack;
	}

	public Consumer<Entity> getEntityAction() {
		return entityAction;
	}

	public Consumer<Triple<Level, BlockPos, Direction>> getBlockAction() {
		return blockAction;
	}

	public Consumer<Tuple<Level, SlotAccess>> getItemAction() {
		return itemAction;
	}

	public Consumer<Tuple<Level, SlotAccess>> getItemActionAfterCrafting() {
		return itemActionAfterCrafting;
	}

	public Predicate<Tuple<Level, ItemStack>> getItemCondition() {
		return itemCondition;
	}

	public ResourceLocation getRecipeIdentifier() {
		return recipeIdentifier;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public boolean doesApply(ResourceLocation recipeId, ItemStack originalResultStack) {
		return (recipeIdentifier == null || recipeIdentifier.equals(recipeId))
			&& (originalResultStack == null || (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), originalResultStack))));
	}

}
