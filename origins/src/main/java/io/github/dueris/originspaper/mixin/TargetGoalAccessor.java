package io.github.dueris.originspaper.mixin;

import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TargetGoal.class)
public interface TargetGoalAccessor {

	@Accessor
	boolean getMustSee();

	@Accessor
	boolean getMustReach();

}
