package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.power.factory.PowerType;
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

public class AttackerActionWhenHitPower extends CooldownPower {
	private final ActionTypeFactory<Entity> entityAction;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;

	public AttackerActionWhenHitPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									  ActionTypeFactory<Entity> entityAction, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, int cooldown, HudRender hudRender) {
		super(key, type, name, description, hidden, condition, loadingPriority, hudRender, cooldown);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("attacker_action_when_hit"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER);
	}

	public boolean doesApply(@NotNull DamageSource source, float amount, Entity entity) {
		return source.getEntity() != null
			&& canUse(entity)
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void whenHit(Entity attacker, @NotNull Entity entity) {
		this.entityAction.accept(attacker);
		use(entity);
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

}
