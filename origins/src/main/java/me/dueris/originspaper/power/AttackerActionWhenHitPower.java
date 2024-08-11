package me.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.HudRender;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.Util;
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

public class AttackerActionWhenHitPower extends PowerType implements CooldownInterface {
	private final ActionFactory<Entity> entityAction;
	private final ConditionFactory<Tuple<DamageSource, Float>> damageCondition;
	private final int cooldown;
	private final HudRender hudRender;

	public AttackerActionWhenHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
									  ActionFactory<Entity> entityAction, ConditionFactory<Tuple<DamageSource, Float>> damageCondition, int cooldown, HudRender hudRender) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
		this.cooldown = cooldown;
		this.hudRender = hudRender;
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("attacker_action_when_hit"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER);
	}

	public boolean doesApply(@NotNull DamageSource source, float amount, Entity entity) {
		return source.getEntity() != null
			&& !CooldownPower.isInCooldown(entity.getBukkitEntity(), this)
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void whenHit(Entity attacker, @NotNull Entity entity) {
		this.entityAction.accept(attacker);
		CooldownPower.addCooldown(entity.getBukkitEntity(), this);
	}

	@EventHandler
	public void whenHit(@NotNull EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player p) {
			net.minecraft.world.entity.player.Player player = ((CraftPlayer) p).getHandle();
			if (!getPlayers().contains(player)) return;
			Entity damager = ((CraftEntity) e.getDamager()).getHandle();
			DamageSource damageSource = Util.damageSourceFromBukkit(e.getDamageSource());
			if (doesApply(damageSource, Double.valueOf(e.getDamage()).floatValue(), player)) {
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
