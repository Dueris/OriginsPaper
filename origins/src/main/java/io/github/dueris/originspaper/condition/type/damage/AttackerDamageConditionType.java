package io.github.dueris.originspaper.condition.type.damage;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.type.DamageConditionType;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AttackerDamageConditionType extends DamageConditionType {

	public static final TypedDataObjectFactory<AttackerDamageConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("entity_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty()),
		data -> new AttackerDamageConditionType(
			data.get("entity_condition")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("entity_condition", conditionType.entityCondition)
	);

	private final Optional<EntityCondition> entityCondition;

	public AttackerDamageConditionType(Optional<EntityCondition> entityCondition) {
		this.entityCondition = entityCondition;
	}

	@Override
	public boolean test(@NotNull DamageSource source, float amount) {
		Entity attacker = source.getEntity();
		return attacker != null
			&& entityCondition.map(condition -> condition.test(attacker)).orElse(true);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return DamageConditionTypes.ATTACKER;
	}

}
