package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.DamageCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnDeathPowerType extends PowerType {

	public static final TypedDataObjectFactory<ActionOnDeathPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("damage_condition", DamageCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new ActionOnDeathPowerType(
			data.get("bientity_action"),
			data.get("bientity_condition"),
			data.get("damage_condition"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_action", powerType.biEntityAction)
			.set("bientity_condition", powerType.biEntityCondition)
			.set("damage_condition", powerType.damageCondition)
	);

	private final Optional<DamageCondition> damageCondition;
	private final Optional<BiEntityCondition> biEntityCondition;
	private final Optional<BiEntityAction> biEntityAction;

	public ActionOnDeathPowerType(Optional<BiEntityAction> biEntityAction, Optional<BiEntityCondition> biEntityCondition, Optional<DamageCondition> damageCondition, Optional<EntityCondition> condition) {
		super(condition);
		this.damageCondition = damageCondition;
		this.biEntityAction = biEntityAction;
		this.biEntityCondition = biEntityCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_DEATH;
	}

	public boolean doesApply(Entity actor, DamageSource damageSource, float damageAmount) {
		return damageCondition.map(condition -> condition.test(damageSource, damageAmount)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(actor, getHolder())).orElse(true);
	}

	public void onDeath(Entity actor) {
		biEntityAction.ifPresent(action -> action.execute(actor, getHolder()));
	}

}
