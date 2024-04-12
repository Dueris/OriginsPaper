package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_lava_speed;

public class ModifyLavaSpeed extends CraftPower {


    @Override
    public void run(Player p) {
        if (modify_lava_speed.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                try {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (ConditionExecutor.testEntity(power.getJsonObjectOrNew("condition"), (CraftEntity) p)) {
                            for (HashMap<String, Object> modifier : power.getModifiers("modifier", "modifiers")) {
                                Float value = Float.valueOf(modifier.get("value").toString());
                                String operation = modifier.get("operation").toString();
                                BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                if (mathOperator != null) {
                                    float result = (float) mathOperator.apply(0.02f, value);
                                    if (power == null) {
                                        getPowerArray().remove(p);
                                        return;
                                    }
                                    if (!getPowerArray().contains(p)) return;
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
