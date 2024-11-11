package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.DamageCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionWhenDamageTakenPowerType extends CooldownPowerType {

	public static final TypedDataObjectFactory<ActionWhenDamageTakenPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE)
			.add("damage_condition", DamageCondition.DATA_TYPE.optional(), Optional.empty())
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 0),
		(data, condition) -> new ActionWhenDamageTakenPowerType(
			data.get("entity_action"),
			data.get("damage_condition"),
			data.get("hud_render"),
			data.get("cooldown"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("damage_condition", powerType.damageCondition)
			.set("hud_render", powerType.getRenderSettings())
			.set("cooldown", powerType.getCooldown())
	);

	private final EntityAction entityAction;
	private final Optional<DamageCondition> damageCondition;

	public ActionWhenDamageTakenPowerType(EntityAction entityAction, Optional<DamageCondition> damageCondition, HudRender hudRender, int cooldownDuration, Optional<EntityCondition> condition) {
		super(cooldownDuration, hudRender, condition);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_WHEN_DAMAGE_TAKEN;
	}

	public boolean doesApply(DamageSource source, float amount) {
		return this.canUse()
			&& damageCondition.map(condition -> condition.test(source, amount)).orElse(true);
	}

	public void whenHit() {
		this.use();
		this.entityAction.execute(getHolder());
	}

}
