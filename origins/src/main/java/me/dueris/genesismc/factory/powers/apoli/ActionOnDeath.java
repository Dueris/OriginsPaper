package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class ActionOnDeath extends PowerType {
	private final FactoryJsonObject bientityAction;
	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject damageCondition;
	private final FactoryJsonObject entityAction;

	public ActionOnDeath(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject bientityAction, FactoryJsonObject bientityCondition, FactoryJsonObject damageCondition, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		this.bientityAction = bientityAction;
		this.bientityCondition = bientityCondition;
		this.damageCondition = damageCondition;
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_on_death"))
			.add("bientity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void d(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (getPlayers().contains(p)) {
				if (!isActive(p) || !ConditionExecutor.testDamage(damageCondition, e.getEntity().getLastDamageCause()))
					return;
				Actions.executeEntity(p, entityAction);
				if (((CraftPlayer) p).getHandle().getLastHurtByMob() != null && ConditionExecutor.testBiEntity(bientityCondition, ((CraftPlayer) p).getHandle().getLastHurtByMob().getBukkitEntity(), p)) {
					Actions.executeBiEntity(((CraftPlayer) p).getHandle().getLastHurtByMob().getBukkitEntity(), p/* player is target? */, bientityAction);
				}
			}
		}
	}

}
