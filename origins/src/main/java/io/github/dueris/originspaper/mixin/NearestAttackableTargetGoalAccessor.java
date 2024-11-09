package io.github.dueris.originspaper.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NearestAttackableTargetGoal.class)
public interface NearestAttackableTargetGoalAccessor extends TargetGoalAccessor {

	@Accessor
	Class<? extends LivingEntity> getTargetType();

	@Accessor
	TargetingConditions getTargetConditions();

	@Accessor
	int getRandomInterval();

}
