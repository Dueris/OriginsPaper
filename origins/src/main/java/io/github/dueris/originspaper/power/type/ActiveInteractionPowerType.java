package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.power.Power;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActiveInteractionPowerType extends InteractionPowerType implements Prioritized<ActiveInteractionPowerType> {

	private final int priority;

	public ActiveInteractionPowerType(Power power, LivingEntity entity, EnumSet<InteractionHand> hands, InteractionResult actionResult, Predicate<Tuple<Level, ItemStack>> itemCondition, Consumer<Tuple<Level, SlotAccess>> heldItemAction, ItemStack itemResult, Consumer<Tuple<Level, SlotAccess>> resultItemAction, int priority) {
		super(power, entity, hands, actionResult, itemCondition, heldItemAction, itemResult, resultItemAction);
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

}
