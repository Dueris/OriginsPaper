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
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Deprecated
public class SelfActionOnHitPowerType extends CooldownPowerType {

	public static final TypedDataObjectFactory<SelfActionOnHitPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE)
			.add("target_condition", EntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("damage_condition", DamageCondition.DATA_TYPE.optional(), Optional.empty())
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 1),
		(data, condition) -> new SelfActionOnHitPowerType(
			data.get("entity_action"),
			data.get("target_condition"),
			data.get("damage_condition"),
			data.get("hud_render"),
			data.get("cooldown"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("target_condition", powerType.targetCondition)
			.set("damage_condition", powerType.damageCondition)
			.set("hud_render", powerType.getRenderSettings())
			.set("cooldown", powerType.getCooldown())
	);

	private final EntityAction entityAction;

	private final Optional<EntityCondition> targetCondition;
	private final Optional<DamageCondition> damageCondition;

	public SelfActionOnHitPowerType(EntityAction entityAction, Optional<EntityCondition> targetCondition, Optional<DamageCondition> damageCondition, HudRender hudRender, int cooldownDuration, Optional<EntityCondition> condition) {
		super(cooldownDuration, hudRender, condition);
		this.damageCondition = damageCondition;
		this.entityAction = entityAction;
		this.targetCondition = targetCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.SELF_ACTION_ON_HIT;
	}

	public boolean doesApply(Entity target, DamageSource source, float amount) {
		return this.canUse()
			&& damageCondition.map(condition -> condition.test(source, amount)).orElse(true)
			&& targetCondition.map(condition -> condition.test(target)).orElse(true);
	}

	public void onHit() {
		this.use();
		entityAction.execute(getHolder());
	}

}
