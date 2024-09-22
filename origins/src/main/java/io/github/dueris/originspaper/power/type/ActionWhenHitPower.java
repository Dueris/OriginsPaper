package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
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

public class ActionWhenHitPower extends CooldownPower {
	private final ActionTypeFactory<Tuple<Entity, Entity>> bientityAction;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition;

	public ActionWhenHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  ActionTypeFactory<Tuple<Entity, Entity>> bientityAction, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, int cooldown, HudRender hudRender, ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, hudRender, cooldown);
		this.bientityAction = bientityAction;
		this.damageCondition = damageCondition;
		this.bientityCondition = bientityCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("action_when_hit"), PowerType.getFactory().getSerializableData()
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null));
	}

	public boolean doesApply(@Nullable Entity attacker, Entity entity, DamageSource source, float amount) {
		return attacker != null
			&& canUse(entity)
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(attacker, entity)))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void whenHit(Entity attacker, Entity entity) {
		this.bientityAction.accept(new Tuple<>(attacker, entity));
		use(entity);
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

}
