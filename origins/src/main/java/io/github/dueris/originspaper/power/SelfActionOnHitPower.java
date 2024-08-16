package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
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

public class SelfActionOnHitPower extends PowerType implements CooldownInterface {
	private final ActionFactory<Entity> entityAction;
	private final ConditionFactory<Entity> targetCondition;
	private final ConditionFactory<Tuple<DamageSource, Float>> damageCondition;
	private final HudRender hudRender;
	private final int cooldown;

	public SelfActionOnHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								ActionFactory<Entity> entityAction, ConditionFactory<Entity> targetCondition, ConditionFactory<Tuple<DamageSource, Float>> damageCondition, HudRender hudRender, int cooldown) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.targetCondition = targetCondition;
		this.damageCondition = damageCondition;
		this.hudRender = hudRender;
		this.cooldown = cooldown;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("self_action_on_hit"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 1);
	}

	public boolean doesApply(Entity target, @NotNull Entity entity, DamageSource source, float amount) {
		return !CooldownPower.isInCooldown(entity.getBukkitEntity(), this)
			&& (targetCondition == null || targetCondition.test(target))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	@EventHandler
	public void onHit(@NotNull EntityDamageByEntityEvent e) {
		Entity entity = ((CraftEntity) e.getDamager()).getHandle();
		if (entity instanceof Player && getPlayers().contains(entity) && isActive(entity)) {
			DamageSource nmsDamageSource = ((CraftDamageSource) e.getDamageSource()).getHandle();
			float amt = Double.valueOf(e.getDamage()).floatValue();
			Entity target = ((CraftEntity) e.getEntity()).getHandle();
			if (doesApply(target, entity, nmsDamageSource, amt)) {
				onHit(entity);
			}
		}
	}

	public void onHit(@NotNull Entity entity) {
		CooldownPower.addCooldown(entity.getBukkitEntity(), this);
		this.entityAction.accept(entity);
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}
}
