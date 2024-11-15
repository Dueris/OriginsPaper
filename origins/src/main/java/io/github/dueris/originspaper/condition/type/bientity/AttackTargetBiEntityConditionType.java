package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Targeting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AttackTargetBiEntityConditionType extends BiEntityConditionType {

	public static boolean condition(Entity actor, Entity target) {
		return (actor instanceof Targeting targeter && Objects.equals(target, targeter.getTarget()))
			|| (actor instanceof NeutralMob angerable && Objects.equals(target, angerable.getTarget()));
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.ATTACK_TARGET;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return condition(actor, target);
	}

}
