package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

// TODO : track thrower
public class PreventItemPickupPower extends PowerType {
	private final ActionFactory<Tuple<Entity, Entity>> biEntityActionItem;
	private final ActionFactory<Tuple<Level, SlotAccess>> itemAction;
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;

	public PreventItemPickupPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								  ActionFactory<Tuple<Entity, Entity>> biEntityActionItem, ActionFactory<Tuple<Level, SlotAccess>> itemAction, ConditionFactory<Tuple<Level, ItemStack>> itemCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.biEntityActionItem = biEntityActionItem;
		this.itemAction = itemAction;
		this.itemCondition = itemCondition;
	}

	public static boolean doesPrevent(ItemEntity itemEntity, Entity entity) {

		if (!PowerHolderComponent.hasPowerType(entity.getBukkitEntity(), PreventItemPickupPower.class)) {
			return false;
		}

		ItemStack stack = itemEntity.getItem();
		boolean prevented = false;

		for (PreventItemPickupPower power : PowerHolderComponent.getPowers(entity.getBukkitEntity(), PreventItemPickupPower.class)) {
			if (power.doesPrevent(stack, entity)) {
				power.executeActions(itemEntity, entity);
				prevented = true;
			}
		}

		return prevented;

	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_item_pickup"))
			.add("bientity_action_item", ApoliDataTypes.BIENTITY_ACTION, null)
			.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null);
	}

	public boolean doesPrevent(ItemStack stack, Entity entity) {
		return (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)));
	}

	public void executeActions(ItemEntity itemEntity, Entity entity) {
		if (itemAction != null) {
			SlotAccess reference = Util.createStackReference(itemEntity.getItem());
			itemAction.accept(new Tuple<>(entity.level(), reference));
			itemEntity.setItem(reference.get());
		}
		if (biEntityActionItem != null) {
			biEntityActionItem.accept(new Tuple<>(entity, itemEntity));
		}
	}
}
