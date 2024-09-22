package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
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
	private final ActionTypeFactory<Tuple<Entity, Entity>> biEntityActionItem;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> itemAction;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;

	public PreventItemPickupPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  ActionTypeFactory<Tuple<Entity, Entity>> biEntityActionItem, ActionTypeFactory<Tuple<Level, SlotAccess>> itemAction, ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.biEntityActionItem = biEntityActionItem;
		this.itemAction = itemAction;
		this.itemCondition = itemCondition;
	}

	public static boolean doesPrevent(ItemEntity itemEntity, @NotNull Entity entity) {

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

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("prevent_item_pickup"), PowerType.getFactory().getSerializableData()
			.add("bientity_action_item", ApoliDataTypes.BIENTITY_ACTION, null)
			.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null));
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
