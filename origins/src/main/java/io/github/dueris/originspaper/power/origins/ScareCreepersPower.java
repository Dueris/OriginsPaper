package io.github.dueris.originspaper.power.origins;

import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ScareCreepersPower {

	public static void modifyGoals(@NotNull PathfinderMob pathAwareEntity) {

		GoalSelector targetSelector = pathAwareEntity.targetSelector;
		GoalSelector goalSelector = pathAwareEntity.goalSelector;

		Iterator<WrappedGoal> oldTargetPrioGoals = targetSelector.getAvailableGoals().iterator();

		// Remove old NearestAttackableTargetGoals so we can add a new one
		while (oldTargetPrioGoals.hasNext()) {

			WrappedGoal oldTargetPrioGoal = oldTargetPrioGoals.next();
			if (!(oldTargetPrioGoal.getGoal() instanceof NearestAttackableTargetGoal)) {
				continue;
			}

			oldTargetPrioGoals.remove();

		}

		goalSelector.addGoal(3, new AvoidEntityGoal<>(pathAwareEntity, LivingEntity.class, e -> PowerHolderComponent.hasPower(e.getBukkitEntity(), "origins:scare_creepers"), 6.0F, 1.0D, 1.2D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
		goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(pathAwareEntity, Player.class, true, (e) -> !PowerHolderComponent.hasPower(e.getBukkitEntity(), "origins:scare_creepers")));

	}
}
