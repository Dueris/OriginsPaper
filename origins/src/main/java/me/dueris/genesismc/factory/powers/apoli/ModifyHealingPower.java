package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.function.BinaryOperator;

public class ModifyHealingPower extends ModifierPower implements Listener {

	public ModifyHealingPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_healing"));
	}

	@EventHandler
	public void runD(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (!getPlayers().contains(p)) return;
			for (Modifier modifier : getModifiers()) {
				Float value = modifier.value();
				String operation = modifier.operation();
				BinaryOperator mathOperator = Utils.getOperationMappingsFloat().get(operation);
				if (mathOperator != null) {
					float result = (float) mathOperator.apply(Double.valueOf(e.getAmount()).floatValue(), value);
					if (isActive(p)) {
						e.setAmount(result);
					}
				}
			}
		}
	}

}
