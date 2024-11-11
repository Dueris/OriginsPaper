package io.github.dueris.originspaper.condition.type.bientity.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.BiEntityConditionType;
import io.github.dueris.originspaper.condition.type.BiEntityConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ConstantBiEntityConditionType extends BiEntityConditionType implements ConstantMetaConditionType {

	private final boolean value;

	public ConstantBiEntityConditionType(boolean value) {
		this.value = value;
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return BiEntityConditionTypes.CONSTANT;
	}

	@Override
	public boolean test(Entity actor, Entity target) {
		return value();
	}

	@Override
	public boolean value() {
		return value;
	}

}
