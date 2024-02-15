package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_swim_speed;

public class ModifySwimSpeedPower extends CraftPower {

    String MODIFYING_KEY = "modify_swim_speed";

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

    @Override
    public void run(Player p) {
        for (LayerContainer layer : CraftApoli.getLayers()) {
            ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
            try {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "apoli:modify_swim_speed", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        if (!p.isSwimming()) return;
                        // Change to use dolphins grace for easier
                        // Vector swimVelocity = p.getLocation().getDirection().normalize().multiply(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY));
                        // p.setVelocity(swimVelocity);
                        int ampl = Math.round(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY));
                        if (ampl < 1) {
                            ampl = 1;
                        }
                        if (ampl > 10) {
                            ampl = 10;
                        }
                        p.addPotionEffect(
                                new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 100, ampl, false, false, false)
                        );
                        setActive(p, power.getTag(), true);
                    } else {
                        setActive(p, power.getTag(), false);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_swim_speed";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_swim_speed;
    }

    public void apply(Player p) {
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if (modify_swim_speed.contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                        Float value = Float.valueOf(modifier.get("value").toString());
                        String operation = modifier.get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(1, value);
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
