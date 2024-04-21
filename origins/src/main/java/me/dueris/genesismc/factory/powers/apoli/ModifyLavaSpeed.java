package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_lava_speed;

public class ModifyLavaSpeed extends CraftPower {

    @Override
    public void run(Player p) {
        if (modify_lava_speed.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                try {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                            for (Modifier modifier : power.getModifiers()) {
                                Float value = modifier.value();
                                String operation = modifier.operation();
                                BinaryOperator<Float> mathOperator = Utils.getOperationMappingsFloat().get(operation);
                                if (mathOperator != null) {
                                    float result = mathOperator.apply(0.02f, value);
                                    setActive(p, power.getTag(), true);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, calculateSpeedAmplifier(Math.toIntExact(Long.valueOf(String.valueOf(result)))), false, false, false));
                                }
                            }

                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ev.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_lava_speed";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_lava_speed;
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
