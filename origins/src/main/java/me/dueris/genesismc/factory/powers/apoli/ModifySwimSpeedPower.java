package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_swim_speed;

public class ModifySwimSpeedPower extends CraftPower {

    private static final ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
    String MODIFYING_KEY = "modify_swim_speed";

    @Override
    public void run(Player p, Power power) {
        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
            if (!p.isSwimming()) return;
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

    @Override
    public String getType() {
        return "apoli:modify_swim_speed";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_swim_speed;
    }

    public void apply(Player p) {
        if (modify_swim_speed.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                    for (Modifier modifier : power.getModifiers()) {
                        Float value = modifier.value();
                        String operation = modifier.operation();
                        BinaryOperator mathOperator = Utils.getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(1, value);
                            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, result);
                        } else {
                            Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for modify_swim_speed!");
                            new Throwable().printStackTrace();
                        }
                    }
                }
            }
        } else {
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }
}
