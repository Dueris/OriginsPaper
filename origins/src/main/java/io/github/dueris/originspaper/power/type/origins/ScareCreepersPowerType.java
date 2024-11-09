package io.github.dueris.originspaper.power.type.origins;

import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.mixin.MobEntityAccessor;
import io.github.dueris.originspaper.mixin.NearestAttackableTargetGoalAccessor;
import io.github.dueris.originspaper.mixin.TargetingConditionsAccessor;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class ScareCreepersPowerType extends PowerType {

	public ScareCreepersPowerType(Power power, LivingEntity entity) {
		super(power, entity);
	}

	public static void modifyGoals(PathfinderMob pathAwareEntity) {

		GoalSelector targetSelector = ((MobEntityAccessor) pathAwareEntity).getTargetSelector();
		GoalSelector goalSelector = ((MobEntityAccessor) pathAwareEntity).getGoalSelector();

		Iterator<WrappedGoal> oldTargetPrioGoals = targetSelector.getAvailableGoals().iterator();
		Set<WrappedGoal> newTargetPrioGoals = new HashSet<>();

		while (oldTargetPrioGoals.hasNext()) {

			WrappedGoal oldTargetPrioGoal = oldTargetPrioGoals.next();
			if (!(oldTargetPrioGoal.getGoal() instanceof NearestAttackableTargetGoalAccessor oldTargetGoal)) {
				continue;
			}

			Predicate<LivingEntity> targetCondition = Util.combineAnd(((TargetingConditionsAccessor) oldTargetGoal.getTargetConditions()).getSelector(), e -> !PowerHolderComponent.hasPowerType(e, ScareCreepersPowerType.class));
			WrappedGoal newTargetPrioGoal = new WrappedGoal(oldTargetPrioGoal.getPriority(), new NearestAttackableTargetGoal<>(pathAwareEntity, oldTargetGoal.getTargetType(), oldTargetGoal.getRandomInterval(), oldTargetGoal.getMustSee(), oldTargetGoal.getMustReach(), targetCondition));

			newTargetPrioGoals.add(newTargetPrioGoal);
			oldTargetPrioGoals.remove();

		}

		goalSelector.addGoal(3, new AvoidEntityGoal<>(pathAwareEntity, LivingEntity.class, e -> PowerHolderComponent.hasPowerType(e, ScareCreepersPowerType.class), 6.0F, 1.0D, 1.2D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
		newTargetPrioGoals.forEach(pg -> targetSelector.addGoal(pg.getPriority(), pg.getGoal()));

	}

}
