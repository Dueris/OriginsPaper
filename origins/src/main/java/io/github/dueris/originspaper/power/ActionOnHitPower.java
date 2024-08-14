package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class ActionOnHitPower extends PowerType implements CooldownInterface {
	private final ActionFactory<Tuple<Entity, Entity>> bientityAction;
	private final ConditionFactory<Tuple<DamageSource, Float>> damageCondition;
	private final int cooldown;
	private final HudRender hudRender;
	private final ConditionFactory<Tuple<Entity, Entity>> bientityCondition;

	public ActionOnHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							ActionFactory<Tuple<Entity, Entity>> bientityAction, ConditionFactory<Tuple<DamageSource, Float>> damageCondition, int cooldown, HudRender hudRender, ConditionFactory<Tuple<Entity, Entity>> bientityCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.bientityAction = bientityAction;
		this.damageCondition = damageCondition;
		this.cooldown = cooldown;
		this.hudRender = hudRender;
		this.bientityCondition = bientityCondition;
	}

	public static InstanceDefiner buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_hit"))
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null);
	}

	public boolean doesApply(Entity target, @NotNull Entity entity, DamageSource source, float amount) {
		return !CooldownPower.isInCooldown(entity.getBukkitEntity(), this)
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(entity, target)))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void onHit(Entity target, Entity entity) {
		this.bientityAction.accept(new Tuple<>(entity, target));
		CooldownPower.addCooldown(entity.getBukkitEntity(), this);
	}

	@EventHandler
	public void onHit(@NotNull EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player p) {
			net.minecraft.world.entity.player.Player player = ((CraftPlayer) p).getHandle();
			if (!getPlayers().contains(player)) return;
			Entity target = ((CraftEntity) e.getEntity()).getHandle();
			DamageSource damageSource = Util.damageSourceFromBukkit(e.getDamageSource());
			if (doesApply(target, player, damageSource, Double.valueOf(e.getDamage()).floatValue()) && isActive(player)) {
				onHit(target, player);
			}
		}
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
