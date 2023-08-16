package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.ErrorSystem;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_damage_dealt;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_lava_speed;

public class ModifyLavaSpeed extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(modify_lava_speed.contains(p)){
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                    try {
                        if (ConditionExecutor.check("bientity_condition", p, origin, "origins:modify_lava_speed", null, p)) {
                            Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_lava_speed").getModifier().get("value").toString());
                            String operation = origin.getPowerFileFromType("origins:modify_lava_speed").getModifier().get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                            if (mathOperator != null) {
                                float result = (float) mathOperator.apply(0.02f, value);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, calculateSpeedAmplifier(Math.toIntExact(Long.valueOf(String.valueOf(result)))), false, false, false));
                            }
                        }
                    } catch (Exception ev) {
                        ErrorSystem errorSystem = new ErrorSystem();
                        errorSystem.throwError("unable to get bi-entity", "origins:modify_lava_speed", p, origin, OriginPlayer.getLayer(p, origin));
                        ev.printStackTrace();
                    }
                }
            }
        }
    }

    public int calculateSpeedAmplifier(float value) {
        float maxValue = 255.0f;
        float minValue = 0.1f;
        int maxAmplifier = 10;
        int minAmplifier = 0;

        float normalizedValue = Math.max(minValue, Math.min(value, maxValue));
        float percentage = (normalizedValue - minValue) / (maxValue - minValue);
        int amplifier = (int) (percentage * (maxAmplifier - minAmplifier)) + minAmplifier;

        return amplifier;
    }
}