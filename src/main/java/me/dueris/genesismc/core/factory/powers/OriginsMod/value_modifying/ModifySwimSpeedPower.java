package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.ErrorSystem;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_swim_speed;

public class ModifySwimSpeedPower extends BukkitRunnable {

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
                    }
                } catch (Exception e){
                    ErrorSystem errorSystem = new ErrorSystem();
                    errorSystem.throwError("unable to set modifier", "origins:modify_swim_speed", p, origin, OriginPlayer.getLayer(p, origin));
                }
            }
        }
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
                        Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.value_modifier_save").replace("%modifier%", MODIFYING_KEY));
                    }
                }
            }
        }else{
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }
}
