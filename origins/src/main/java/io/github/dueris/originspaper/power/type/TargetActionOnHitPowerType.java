package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TargetActionOnHitPowerType extends CooldownPowerType {

	private final Predicate<Tuple<DamageSource, Float>> damageCondition;
	private final Predicate<Entity> targetCondition;
	private final Consumer<Entity> entityAction;

	public TargetActionOnHitPowerType(Power power, LivingEntity entity, int cooldownDuration, HudRender hudRender, Predicate<Tuple<DamageSource, Float>> damageCondition, Consumer<Entity> entityAction, Predicate<Entity> targetCondition) {
		super(power, entity, cooldownDuration, hudRender);
		this.damageCondition = damageCondition;
		this.entityAction = entityAction;
		this.targetCondition = targetCondition;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("target_action_on_hit"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("cooldown", SerializableDataTypes.INT, 1)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null),
			data -> (power, entity) -> new TargetActionOnHitPowerType(power, entity,
				data.get("cooldown"),
				data.get("hud_render"),
				data.get("damage_condition"),
				data.get("entity_action"),
				data.get("target_condition")
			)
		).allowCondition();
	}

	public boolean doesApply(Entity target, DamageSource source, float amount) {
		return this.canUse()
			&& (targetCondition == null || targetCondition.test(target))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void onHit(Entity target) {
		this.entityAction.accept(target);
		this.use();
	}

}
