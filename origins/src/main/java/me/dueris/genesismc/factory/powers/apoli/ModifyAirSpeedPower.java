package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_air_speed;

public class ModifyAirSpeedPower extends CraftPower {

	String MODIFYING_KEY = "modify_air_speed";


	@Override
	public void run(Player p) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
			try {
				ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						if (power == null) {
							getPowerArray().remove(p);
							return;
						}
						if (!getPowerArray().contains(p)) return;
						setActive(p, power.getTag(), true);
						p.setFlySpeed(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY));
					} else {
						if (power == null) {
							getPowerArray().remove(p);
							return;
						}
						if (!getPowerArray().contains(p)) return;
						setActive(p, power.getTag(), false);
						p.setFlySpeed(valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:modify_air_speed";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return modify_air_speed;
	}

	public void apply(Player p) {
		ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
		if (modify_air_speed.contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					for (HashMap<String, Object> modifier : power.getJsonListSingularPlural("modifier", "modifiers")) {
						Float value = Float.valueOf(modifier.get("value").toString());
						String operation = modifier.get("operation").toString();
						BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
						if (mathOperator != null) {
							float result = (float) mathOperator.apply(valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY), value);
							valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, result);
						} else {
							Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.value_modifier_save").replace("%modifier%", MODIFYING_KEY));
						}
					}
				}

			}
		} else {
			valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
		}
	}
}
