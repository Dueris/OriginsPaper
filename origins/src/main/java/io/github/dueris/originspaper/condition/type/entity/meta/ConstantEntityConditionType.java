package io.github.dueris.originspaper.condition.type.entity.meta;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ConstantEntityConditionType extends EntityConditionType implements ConstantMetaConditionType {

	private final boolean value;

	public ConstantEntityConditionType(boolean value) {
		this.value = value;
	}

	@Override
	public boolean test(Entity entity) {
		return value();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.CONSTANT;
	}

	@Override
	public boolean value() {
		return value;
	}

}
