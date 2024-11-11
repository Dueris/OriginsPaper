package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import net.minecraft.tags.DamageTypeTags;
import org.jetbrains.annotations.NotNull;

public class UnblockableDamageConditionType extends InTagDamageConditionType {

	public UnblockableDamageConditionType() {
		super(DamageTypeTags.BYPASSES_SHIELD);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.UNBLOCKABLE;
	}

}
