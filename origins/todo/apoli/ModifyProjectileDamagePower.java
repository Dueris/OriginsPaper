package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.Actions;
import me.dueris.originspaper.factory.data.types.modifier.Modifier;
import me.dueris.originspaper.util.Util;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BinaryOperator;

public class ModifyProjectileDamagePower extends ModifierPower implements Listener {

	private final FactoryJsonObject targetCondition;
	private final FactoryJsonObject damageCondition;
	private final FactoryJsonObject targetAction;
	private final FactoryJsonObject selfAction;

	public ModifyProjectileDamagePower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonObject targetCondition, FactoryJsonObject damageCondition, FactoryJsonObject targetAction, FactoryJsonObject selfAction) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.targetCondition = targetCondition;
		this.damageCondition = damageCondition;
		this.targetAction = targetAction;
		this.selfAction = selfAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_projectile_damage"))
			.add("target_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("target_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("self_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void runD(@NotNull EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Projectile p && p.getShooter() instanceof Player pl) {
			if (getPlayers().contains(pl)) {
				try {
					if (isActive(pl) && ConditionExecutor.testEntity(targetCondition, (CraftEntity) e.getEntity()) && ConditionExecutor.testDamage(damageCondition, e)) {
						for (Modifier modifier : getModifiers()) {
							float value = modifier.value();
							String operation = modifier.operation();
							BinaryOperator<Double> mathOperator = Util.getOperationMappingsDouble().get(operation);
							if (mathOperator != null) {
								ModifyDamageDealtPower.runSetDMG(e, operation, value);
								Actions.executeEntity(e.getEntity(), targetAction);
								Actions.executeEntity(pl, selfAction);
							}
						}
					}
				} catch (Exception ev) {
					ev.printStackTrace();
				}
			}
		}
	}

}
