package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ActionOnDeathPower extends PowerType {
	private final ActionFactory<Tuple<Entity, Entity>> bientityAction;
	private final ConditionFactory<Tuple<DamageSource, Float>> damageCondition;
	private final ConditionFactory<Tuple<Entity, Entity>> bientityCondition;

	public ActionOnDeathPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							  ActionFactory<Tuple<Entity, Entity>> bientityAction, ConditionFactory<Tuple<DamageSource, Float>> damageCondition, ConditionFactory<Tuple<Entity, Entity>> bientityCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.bientityAction = bientityAction;
		this.damageCondition = damageCondition;
		this.bientityCondition = bientityCondition;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_death"))
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null);
	}

	public boolean doesApply(Entity actor, DamageSource damageSource, float damageAmount, Entity entity) {
		return (bientityCondition == null || bientityCondition.test(new Tuple<>(actor, entity)))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(damageSource, damageAmount))) && isActive(entity);
	}

	public void onDeath(Entity actor, Entity entity) {
		bientityAction.accept(new Tuple<>(actor, entity));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamage(@NotNull EntityDamageEvent e) {
		if (e.getEntity() instanceof org.bukkit.entity.Player p) {
			Player player = ((CraftPlayer) p).getHandle();
			if (!((player.getHealth() - e.getFinalDamage()) <= 0.0F) || !getPlayers().contains(player)) return;
			new BukkitRunnable() {
				@Override
				public void run() {
					DamageSource source = Util.damageSourceFromBukkit(e.getDamageSource());
					Double doubleAmount = e.getDamage();

					if (doesApply(source.getEntity(), source, doubleAmount.floatValue(), player)) {
						onDeath(source.getEntity(), player);
					}
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 2);
		}
	}
}
