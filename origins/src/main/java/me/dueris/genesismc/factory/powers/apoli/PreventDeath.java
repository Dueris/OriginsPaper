package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PreventDeath extends PowerType implements Listener {
	private final FactoryJsonObject damageCondition;
	private final FactoryJsonObject entityAction;

	public PreventDeath(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject damageCondition, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		this.damageCondition = damageCondition;
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("prevent_death"))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void run(PlayerDeathEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (isActive(e.getPlayer()) && ConditionExecutor.testDamage(damageCondition, e.getEntity().getLastDamageCause())) {
				e.setCancelled(true);
				Actions.executeEntity(e.getPlayer(), entityAction);
			}
		}
	}

}
