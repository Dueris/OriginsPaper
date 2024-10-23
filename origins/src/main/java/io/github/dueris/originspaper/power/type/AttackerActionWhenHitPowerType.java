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
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AttackerActionWhenHitPowerType extends CooldownPowerType {

	private final Predicate<Tuple<DamageSource, Float>> damageCondition;
	private final Consumer<Entity> entityAction;

	public AttackerActionWhenHitPowerType(Power power, LivingEntity entity, int cooldownDuration, HudRender hudRender, Predicate<Tuple<DamageSource, Float>> damageCondition, Consumer<Entity> entityAction) {
		super(power, entity, cooldownDuration, hudRender);
		this.damageCondition = damageCondition;
		this.entityAction = entityAction;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("attacker_action_when_hit"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("cooldown", SerializableDataTypes.INT, 1)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER),
			data -> (power, entity) -> new AttackerActionWhenHitPowerType(
				power,
				entity,
				data.get("cooldown"),
				data.get("hud_render"),
				data.get("damage_condition"),
				data.get("entity_action")
			)
		).allowCondition();
	}

	public boolean doesApply(@NotNull DamageSource source, float amount) {
		return source.getEntity() != null
			&& this.canUse()
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void whenHit(Entity attacker) {
		this.entityAction.accept(attacker);
		this.use();
	}

	@Deprecated(forRemoval = true)
	public void whenHit(DamageSource damageSource, float damageAmount) {
		if (damageSource.getEntity() != null && damageSource.getEntity() != entity) {
			if (damageCondition == null || damageCondition.test(new Tuple<>(damageSource, damageAmount))) {
				if (canUse()) {
					this.entityAction.accept(damageSource.getEntity());
					use();
				}
			}
		}
	}

}

