package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_effect_duration;

public class ModifyStatusEffectDurationPower extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }


    @EventHandler
    public void runD(EntityPotionEffectEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (!modify_effect_duration.contains(p)) return;
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        setActive(p, power.getTag(), true);
                        if (power.getStringOrDefault("status_effect", null) != null) {
                            if (e.getNewEffect().getType().equals(PotionEffectType.getByName(power.getStringOrDefault("status_effect", null)))) {
                                PotionEffect effect = e.getNewEffect();
                                for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                    Float value = Float.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                    if (mathOperator != null) {
                                        float result = (float) mathOperator.apply(effect.getDuration(), value);
                                        effect.withDuration(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                                    }
                                }

                            }
                        } else {
                            for (PotionEffect effect : p.getActivePotionEffects()) {
                                for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                    Float value = Float.valueOf(modifier.get("value").toString());
                                    String operation = modifier.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                    if (mathOperator != null) {
                                        float result = (float) mathOperator.apply(effect.getDuration(), value);
                                        effect.withDuration(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                                    }
                                }
                            }
                        }
                    } else {
                        setActive(p, power.getTag(), false);
                    }
                }

            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_status_effect_duration";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_effect_duration;
    }
}
