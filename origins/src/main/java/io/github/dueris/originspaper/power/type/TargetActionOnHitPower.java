package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class TargetActionOnHitPower extends CooldownPower {
	private final ActionTypeFactory<Entity> entityAction;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;
	private final ConditionTypeFactory<Entity> targetCondition;

	public TargetActionOnHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  ActionTypeFactory<Entity> entityAction, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, int cooldown, HudRender hudRender, ConditionTypeFactory<Entity> targetCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, hudRender, cooldown);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
		this.targetCondition = targetCondition;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("target_action_on_hit"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null);
	}

	public boolean doesApply(Entity target, @NotNull Entity entity, DamageSource source, float amount) {
		return canUse(entity) && (targetCondition == null || targetCondition.test(target))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void onHit(Entity target, @NotNull Entity entity) {
		this.entityAction.accept(target);
		use(entity);
	}

	@EventHandler
	public void onHit(@NotNull EntityDamageByEntityEvent e) {
		Entity entity = ((CraftEntity) e.getDamager()).getHandle();
		if (entity instanceof Player && getPlayers().contains(entity) && isActive(entity)) {
			DamageSource nmsDamageSource = ((CraftDamageSource) e.getDamageSource()).getHandle();
			float amt = Double.valueOf(e.getDamage()).floatValue();
			Entity target = ((CraftEntity) e.getEntity()).getHandle();
			if (doesApply(target, entity, nmsDamageSource, amt)) {
				onHit(target, entity);
			}
		}
	}

}
