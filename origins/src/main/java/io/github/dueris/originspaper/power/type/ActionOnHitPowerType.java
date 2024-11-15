package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.DamageCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnHitPowerType extends CooldownPowerType {

	public static final TypedDataObjectFactory<ActionOnHitPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_action", BiEntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty())
			.add("damage_condition", DamageCondition.DATA_TYPE.optional(), Optional.empty())
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 1),
		(data, condition) -> new ActionOnHitPowerType(
			data.get("bientity_action"),
			data.get("bientity_condition"),
			data.get("damage_condition"),
			data.get("hud_render"),
			data.get("cooldown"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_action", powerType.biEntityAction)
			.set("bientity_condition", powerType.biEntityCondition)
			.set("damage_condition", powerType.damageCondition)
			.set("hud_render", powerType.getRenderSettings())
			.set("cooldown", powerType.getCooldown())
	);

	private final Optional<BiEntityAction> biEntityAction;

	private final Optional<BiEntityCondition> biEntityCondition;
	private final Optional<DamageCondition> damageCondition;

	public ActionOnHitPowerType(Optional<BiEntityAction> biEntityAction, Optional<BiEntityCondition> biEntityCondition, Optional<DamageCondition> damageCondition, HudRender hudRender, int cooldown, Optional<EntityCondition> condition) {
		super(cooldown, hudRender, condition);
		this.damageCondition = damageCondition;
		this.biEntityAction = biEntityAction;
		this.biEntityCondition = biEntityCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.ACTION_ON_HIT;
	}

	public boolean doesApply(Entity target, DamageSource source, float amount) {
		return super.canUse()
			&& damageCondition.map(condition -> condition.test(source, amount)).orElse(true)
			&& biEntityCondition.map(condition -> condition.test(getHolder(), target)).orElse(true);
	}

	public void onHit(Entity target) {
		this.use();
		biEntityAction.ifPresent(action -> action.execute(getHolder(), target));
	}

}
