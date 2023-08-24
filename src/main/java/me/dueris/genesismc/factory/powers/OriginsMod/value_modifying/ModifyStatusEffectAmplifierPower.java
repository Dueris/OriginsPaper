package me.dueris.genesismc.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_effect_amplifier;

public class ModifyStatusEffectAmplifierPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @EventHandler
    public void run(EntityPotionEffectEvent e){
        if(e.getEntity() instanceof Player p){
            if(!modify_effect_amplifier.contains(p)) return;
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                ConditionExecutor executor = new ConditionExecutor();
                if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    if(origin.getPowerFileFromType("origins:modify_status_effect_amplifier").get("status_effect", null) != null){
                        if(e.getNewEffect().getType().equals(PotionEffectType.getByName(origin.getPowerFileFromType("origins:modify_status_effect_amplifier").get("status_effect", null)))){
                            PotionEffect effect = e.getNewEffect();
                            for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_status_effect_amplifier").getPossibleModifiers("modifier", "modifiers")){
                                Float value = Float.valueOf(modifier.get("value").toString());
                                String operation = modifier.get("operation").toString();
                                BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                if (mathOperator != null) {
                                    float result = (float) mathOperator.apply(effect.getAmplifier(), value);
                                    effect.withAmplifier(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                                }
                            }

                        }
                    }else{
                        for(PotionEffect effect : p.getActivePotionEffects()){
                            for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_status_effect_amplifier").getPossibleModifiers("modifier", "modifiers")){
                                Float value = Float.valueOf(modifier.get("value").toString());
                                String operation = modifier.get("operation").toString();
                                BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                if (mathOperator != null) {
                                    float result = (float) mathOperator.apply(effect.getAmplifier(), value);
                                    effect.withAmplifier(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                                }
                            }
                        }
                    }
                }else{
                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_status_effect_amplifier";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_effect_amplifier;
    }
}
