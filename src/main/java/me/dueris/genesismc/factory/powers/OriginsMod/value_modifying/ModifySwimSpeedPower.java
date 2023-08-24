package me.dueris.genesismc.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.translation.LangConfig;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_swim_speed;

public class ModifySwimSpeedPower extends CraftPower {

    @Override
    public void setActive(Boolean bool){
        if(powers_active.containsKey(getPowerFile())){
            powers_active.replace(getPowerFile(), bool);
        }else{
            powers_active.put(getPowerFile(), bool);
        }
    }

    @Override
    public Boolean getActive(){
        return powers_active.get(getPowerFile());
    }

    String MODIFYING_KEY = "modify_swim_speed";

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try{
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("condition", "conditions", p, origin, "origins:modify_swim_speed", null, p)){
                        if(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY) == -1) return;
                        if(!p.isSwimming()) return;
                        Vector swimVelocity = p.getLocation().getDirection().normalize().multiply(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY));
                        p.setVelocity(swimVelocity);
                        setActive(true);
                    }else{
                        setActive(false);
                    }
                } catch (Exception e){
                    ErrorSystem errorSystem = new ErrorSystem();
                    errorSystem.throwError("unable to set modifier", "origins:modify_swim_speed", p, origin, OriginPlayer.getLayer(p, origin));
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_swim_speed";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_swim_speed;
    }

    public void apply(Player p){
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if(modify_swim_speed.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_swim_speed").getPossibleModifiers("modifier", "modifiers")){
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
        }else{
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }
}
