package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class ModifyDamageDealtPower extends CraftPower implements Listener {


	@EventHandler(priority = EventPriority.HIGHEST)
	public void damageEVENT(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) return;
		if (e.getDamager() instanceof Player p && modify_damage_dealt.contains(p)) {
			try {
				for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
					if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) continue;
					if (!ConditionExecutor.testEntity(power.getJsonObject("target_condition"), (CraftEntity) e.getEntity()))
						continue;
					if (!ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) p, (CraftEntity) e.getEntity()))
						continue;
					if (!ConditionExecutor.testDamage(power.getJsonObject("damage_condition"), e)) continue;
					for (Modifier modifier : power.getModifiers()) {
						float value = modifier.value();
						String operation = modifier.operation();
						runSetDMG(e, operation, value);
						setActive(p, power.getTag(), true);
					}
					Actions.executeBiEntity(p, e.getEntity(), power.getJsonObject("bientity_action"));
					Actions.executeEntity(e.getEntity(), power.getJsonObject("target_action"));
					Actions.executeEntity(p, power.getJsonObject("self_action"));
				}
			} catch (Exception ev) {
				ev.printStackTrace();
			}
		}
	}

	public void runSetDMG(EntityDamageByEntityEvent e, String operation, float value) {
		double damage = e.getDamage();
		BinaryOperator<Float> floatOperator = Utils.getOperationMappingsFloat().get(operation);
		if (floatOperator != null) {
			float newDamage = floatOperator.apply((float) damage, value);
			e.setDamage(newDamage);
		}
	}

	@Override
	public String getType() {
		return "apoli:modify_damage_dealt";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return modify_damage_dealt;
	}
}
