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
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionWhenHitPowerType extends CooldownPowerType {

	private final Predicate<Tuple<DamageSource, Float>> damageCondition;
	private final Predicate<Tuple<Entity, Entity>> bientityCondition;
	private final Consumer<Tuple<Entity, Entity>> bientityAction;

	public ActionWhenHitPowerType(Power power, LivingEntity entity, Consumer<Tuple<Entity, Entity>> bientityAction, Predicate<Tuple<Entity, Entity>> bientityCondition, Predicate<Tuple<DamageSource, Float>> damageCondition, HudRender hudRender, int cooldownDuration) {
		super(power, entity, cooldownDuration, hudRender);
		this.damageCondition = damageCondition;
		this.bientityAction = bientityAction;
		this.bientityCondition = bientityCondition;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_when_hit"),
			new SerializableData()
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("cooldown", SerializableDataTypes.INT, 1),
			data -> (power, entity) -> new ActionWhenHitPowerType(power, entity,
				data.get("bientity_action"),
				data.get("bientity_condition"),
				data.get("damage_condition"),
				data.get("hud_render"),
				data.get("cooldown")
			)
		).allowCondition();
	}

	public boolean doesApply(@Nullable Entity attacker, DamageSource source, float amount) {
		return attacker != null
			&& this.canUse()
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(attacker, entity)))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void whenHit(Entity attacker) {
		this.use();
		this.bientityAction.accept(new Tuple<>(attacker, entity));
	}

}

