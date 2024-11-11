package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import net.minecraft.tags.DamageTypeTags;
import org.jetbrains.annotations.NotNull;

@Deprecated(forRemoval = true)
public class OutOfWorldDamageConditionType extends InTagDamageConditionType {

	public OutOfWorldDamageConditionType() {
		super(DamageTypeTags.BYPASSES_INVULNERABILITY);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.OUT_OF_WORLD;
	}

}
