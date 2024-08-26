package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
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
import org.jetbrains.annotations.Nullable;

public class ActionWhenHitPower extends PowerType implements CooldownInterface {
	private final ActionTypeFactory<Tuple<Entity, Entity>> bientityAction;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;
	private final int cooldown;
	private final HudRender hudRender;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition;

	public ActionWhenHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  ActionTypeFactory<Tuple<Entity, Entity>> bientityAction, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, int cooldown, HudRender hudRender, ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.bientityAction = bientityAction;
		this.damageCondition = damageCondition;
		this.cooldown = cooldown;
		this.hudRender = hudRender;
		this.bientityCondition = bientityCondition;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_when_hit"))
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null);
	}

	public boolean doesApply(@Nullable Entity attacker, Entity entity, DamageSource source, float amount) {
		return attacker != null
			&& !CooldownPower.isInCooldown(entity.getBukkitEntity(), this)
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(attacker, entity)))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void whenHit(Entity attacker, Entity entity) {
		this.bientityAction.accept(new Tuple<>(attacker, entity));
		CooldownPower.addCooldown(entity.getBukkitEntity(), this);
	}

	@EventHandler
	public void whenHit(@NotNull EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player p) {
			net.minecraft.world.entity.player.Player player = ((CraftPlayer) p).getHandle();
			if (!getPlayers().contains(player)) return;
			Entity damager = ((CraftEntity) e.getDamager()).getHandle();
			DamageSource damageSource = Util.damageSourceFromBukkit(e.getDamageSource());
			if (doesApply(damager, player, damageSource, Double.valueOf(e.getDamage()).floatValue()) && isActive(player)) {
				whenHit(damager, player);
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
