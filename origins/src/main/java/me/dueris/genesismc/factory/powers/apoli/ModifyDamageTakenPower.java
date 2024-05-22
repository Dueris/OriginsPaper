package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.util.Util;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.function.BinaryOperator;

public class ModifyDamageTakenPower extends ModifierPower implements Listener {
	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject damageCondition;
	private final FactoryJsonObject bientityAction;
	private final FactoryJsonObject attackerAction;
	private final FactoryJsonObject selfAction;

	public ModifyDamageTakenPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonObject bientityCondition, FactoryJsonObject damageCondition, FactoryJsonObject bientityAction, FactoryJsonObject attackerAction, FactoryJsonObject selfAction) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.bientityCondition = bientityCondition;
		this.damageCondition = damageCondition;
		this.bientityAction = bientityAction;
		this.attackerAction = attackerAction;
		this.selfAction = selfAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_damage_taken"))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("attacker_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("self_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void damageEVENT(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (e.getEntity() instanceof Player p && getPlayers().contains(p)) {
			try {
				if (!isActive(p)) return;
				if (e instanceof EntityDamageByEntityEvent ev) {
					if (!ConditionExecutor.testBiEntity(bientityCondition, (CraftEntity) ev.getDamager(), (CraftEntity) p))
						return;
				}
				if (!ConditionExecutor.testDamage(damageCondition, e)) return;
				for (Modifier modifier : getModifiers()) {
					float value = modifier.value();
					String operation = modifier.operation();
					runSetDMG(e, operation, value);
					if (e instanceof EntityDamageByEntityEvent ev) {
						Actions.executeBiEntity(ev.getDamager(), p, bientityAction);
						Actions.executeEntity(ev.getDamager(), attackerAction);
					}
					Actions.executeEntity(p, selfAction);
				}
			} catch (Exception ev) {
				ev.printStackTrace();
			}
		}
	}

	public void runSetDMG(EntityDamageEvent e, String operation, Object value) {
		double damage = e.getDamage();

		BinaryOperator<Float> floatOperator = Util.getOperationMappingsFloat().get(operation);
		if (floatOperator != null) {
			float newDamage = floatOperator.apply((float) damage, (Float) value);
			e.setDamage(newDamage);
		}
	}

}
