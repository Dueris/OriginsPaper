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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class SelfActionOnKillPower extends CooldownPower {
	private final ActionTypeFactory<Entity> entityAction;
	private final ConditionTypeFactory<Entity> targetCondition;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;

	public SelfActionOnKillPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								 ActionTypeFactory<Entity> entityAction, ConditionTypeFactory<Entity> targetCondition, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, HudRender hudRender, int cooldown) {
		super(key, type, name, description, hidden, condition, loadingPriority, hudRender, cooldown);
		this.entityAction = entityAction;
		this.targetCondition = targetCondition;
		this.damageCondition = damageCondition;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("self_action_on_kill"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 1);
	}

	public boolean doesApply(Entity target, @NotNull Entity entity, DamageSource source, float amount) {
		return canUse(entity)
			&& (targetCondition == null || targetCondition.test(target))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void executeAction(@NotNull Entity entity) {
		use(entity);
		entityAction.accept(entity);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(@NotNull EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof org.bukkit.entity.Player p) {
			Player player = ((CraftPlayer) p).getHandle();
			LivingEntity entity = ((CraftLivingEntity) e.getEntity()).getHandle();
			if (!((entity.getHealth() - e.getFinalDamage()) <= 0.0F) || !getPlayers().contains(player) || !isActive(player))
				return;
			DamageSource source = Util.damageSourceFromBukkit(e.getDamageSource());
			Double doubleAmount = e.getDamage();
			if (doesApply(entity, player, source, doubleAmount.floatValue())) {
				executeAction(player);
			}
		}
	}

}
