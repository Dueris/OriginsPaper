package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_air_speed;

public class ModifyAirSpeedPower extends CraftPower {

    String MODIFYING_KEY = "modify_air_speed";

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {
        for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
            ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
            try {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "origins:modify_air_speed", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
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
                ErrorSystem errorSystem = new ErrorSystem();
                errorSystem.throwError("unable to set modifier", "origins:modify_air_speed", p, origin, OriginPlayerUtils.getLayer(p, origin));
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_air_speed";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_air_speed;
    }

    public void apply(Player p) {
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if (modify_air_speed.contains(p)) {
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    for (HashMap<String, Object> modifier : power.getConditionFromString("modifier", "modifiers")) {
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
