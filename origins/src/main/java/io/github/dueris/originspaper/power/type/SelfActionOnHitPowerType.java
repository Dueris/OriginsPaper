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

public class SelfActionOnHitPowerType extends CooldownPowerType {

	private final Predicate<Tuple<DamageSource, Float>> damageCondition;
	private final Predicate<Entity> targetCondition;
	private final Consumer<Entity> entityAction;

	public SelfActionOnHitPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Predicate<Entity> targetCondition, Predicate<Tuple<DamageSource, Float>> damageCondition, HudRender hudRender, int cooldownDuration) {
		super(power, entity, cooldownDuration, hudRender);
		this.damageCondition = damageCondition;
		this.entityAction = entityAction;
		this.targetCondition = targetCondition;
	}

	public boolean doesApply(Entity target, DamageSource source, float amount) {
		return this.canUse()
			&& (targetCondition == null || targetCondition.test(target))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void onHit() {
		this.use();
		this.entityAction.accept(entity);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(OriginsPaper.apoliIdentifier("self_action_on_hit"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
				.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("cooldown", SerializableDataTypes.INT, 1),
			data -> (power, entity) -> new SelfActionOnHitPowerType(power, entity,
				data.get("entity_action"),
				data.get("target_condition"),
				data.get("damage_condition"),
				data.get("hud_render"),
				data.get("cooldown")
			)
		).allowCondition();
	}

}
