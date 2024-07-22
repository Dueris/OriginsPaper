package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class ActionOnDeath extends PowerType {
	private final FactoryJsonObject bientityAction;
	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject damageCondition;

	public ActionOnDeath(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject bientityAction, FactoryJsonObject bientityCondition, FactoryJsonObject damageCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.bientityAction = bientityAction;
		this.bientityCondition = bientityCondition;
		this.damageCondition = damageCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("action_on_death"))
			.add("bientity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void d(@NotNull EntityDeathEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (getPlayers().contains(p)) {
				if (!isActive(p) || !ConditionExecutor.testDamage(damageCondition, e.getEntity().getLastDamageCause()))
					return;
				if (ConditionExecutor.testBiEntity(bientityCondition, ((CraftPlayer) p).getHandle().getLastHurtByMob().getBukkitEntity(), p)) {
					Actions.executeBiEntity(((CraftPlayer) p).getHandle().getLastHurtByMob().getBukkitEntity(), p/* player is target? */, bientityAction);
				}
			}
		}
	}

}
