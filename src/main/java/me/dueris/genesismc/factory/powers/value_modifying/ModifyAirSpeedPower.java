package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public ModifyAirSpeedPower(){
        this.p = p;
    }

    @Override
    public void run(Player p) {
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
            try {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_air_speed", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                    if (origin.getPowerFileFromType(getPowerFile()) == null) {
                        getPowerArray().remove(p);
                        return;
                    }
                    if (!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    p.setFlySpeed(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY));
                } else {
                    if (origin.getPowerFileFromType(getPowerFile()) == null) {
                        getPowerArray().remove(p);
                        return;
                    }
                    if (!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    p.setFlySpeed(valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
                }
            } catch (Exception e) {
                ErrorSystem errorSystem = new ErrorSystem();
                errorSystem.throwError("unable to set modifier", "origins:modify_air_speed", p, origin, OriginPlayer.getLayer(p, origin));
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
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_air_speed").getConditionFromString("modifier", "modifiers")) {
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
        } else {
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }
}
