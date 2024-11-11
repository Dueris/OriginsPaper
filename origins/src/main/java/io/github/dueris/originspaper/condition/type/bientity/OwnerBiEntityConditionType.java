package io.github.dueris.originspaper.condition.type.bientity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OwnerBiEntityConditionType extends BiEntityConditionType {

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.OWNER;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return condition(actor, target);
	}

	public static boolean condition(Entity actor, Entity target) {
		return (target instanceof OwnableEntity tameable && Objects.equals(actor, tameable.getOwner()))
			|| (target instanceof TraceableEntity ownable && Objects.equals(actor, ownable.getOwner()));
	}

}
