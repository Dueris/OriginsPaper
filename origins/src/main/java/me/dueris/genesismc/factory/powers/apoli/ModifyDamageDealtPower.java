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

import java.util.function.BinaryOperator;

public class ModifyDamageDealtPower extends ModifierPower implements Listener {
	private final FactoryJsonObject targetCondition;
	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject damageCondition;
	private final FactoryJsonObject bientityAction;
	private final FactoryJsonObject targetAction;
	private final FactoryJsonObject selfAction;

	public ModifyDamageDealtPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonObject targetCondition, FactoryJsonObject bientityCondition, FactoryJsonObject damageCondition, FactoryJsonObject bientityAction, FactoryJsonObject targetAction, FactoryJsonObject selfAction) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.targetCondition = targetCondition;
		this.bientityCondition = bientityCondition;
		this.damageCondition = damageCondition;
		this.bientityAction = bientityAction;
		this.targetAction = targetAction;
		this.selfAction = selfAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_damage_dealt"))
			.add("target_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("target_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("self_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	public static void runSetDMG(EntityDamageByEntityEvent e, String operation, float value) {
		double damage = e.getDamage();
		BinaryOperator<Float> floatOperator = Util.getOperationMappingsFloat().get(operation);
		if (floatOperator != null) {
			float newDamage = floatOperator.apply((float) damage, value);
			e.setDamage(newDamage);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void damageEVENT(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) return;
		if (e.getDamager() instanceof Player p && getPlayers().contains(p)) {
			try {
				if (!isActive(p)) return;
				if (!ConditionExecutor.testEntity(targetCondition, (CraftEntity) e.getEntity())) return;
				if (!ConditionExecutor.testBiEntity(bientityCondition, (CraftEntity) p, (CraftEntity) e.getEntity()))
					return;
				if (!ConditionExecutor.testDamage(damageCondition, e)) return;
				for (Modifier modifier : getModifiers()) {
					float value = modifier.value();
					String operation = modifier.operation();
					runSetDMG(e, operation, value);
				}
				Actions.executeBiEntity(p, e.getEntity(), bientityAction);
				Actions.executeEntity(e.getEntity(), targetAction);
				Actions.executeEntity(p, selfAction);
			} catch (Exception ev) {
				ev.printStackTrace();
			}
		}
	}

}
