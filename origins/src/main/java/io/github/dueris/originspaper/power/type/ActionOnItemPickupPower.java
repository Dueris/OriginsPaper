package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ActionOnItemPickupPower extends PowerType {
	private final ActionTypeFactory<Tuple<Entity, Entity>> biEntityAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> itemAction;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;

	public ActionOnItemPickupPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								   ActionTypeFactory<Tuple<Entity, Entity>> biEntityAction, ActionTypeFactory<Tuple<Level, SlotAccess>> itemAction, ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition, ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.biEntityAction = biEntityAction;
		this.itemAction = itemAction;
		this.biEntityCondition = biEntityCondition;
		this.itemCondition = itemCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("action_on_item_pickup"), PowerType.getFactory().getSerializableData()
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
			.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null));
	}

	public boolean doesApply(ItemStack stack, Entity entity) {
		return (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)))
			&& (biEntityCondition == null || biEntityCondition.test(new Tuple<>(null/*we dont have access ot the last thrower :l*/, entity)));
	}

	public void executeActions(ItemStack stack, Entity entity) {
		if (itemAction != null)
			itemAction.accept(new Tuple<>(entity.level(), Util.getStackReferenceFromStack(entity, stack)));
		if (biEntityAction != null) biEntityAction.accept(new Tuple<>(null, entity));
	}

}
