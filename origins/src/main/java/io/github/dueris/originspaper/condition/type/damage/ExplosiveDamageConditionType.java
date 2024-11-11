package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import net.minecraft.tags.DamageTypeTags;
import org.jetbrains.annotations.NotNull;

@Deprecated(forRemoval = true)
public class ExplosiveDamageConditionType extends InTagDamageConditionType {

	public ExplosiveDamageConditionType() {
		super(DamageTypeTags.IS_EXPLOSION);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.EXPLOSIVE;
	}

}
