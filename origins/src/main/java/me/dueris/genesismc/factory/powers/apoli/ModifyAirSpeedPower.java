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

import java.util.ArrayList;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_air_speed;

public class ModifyAirSpeedPower extends CraftPower {
    private static String MODIFYING_KEY = "modify_air_speed";
    private static ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();

    @Override
    public void run(Player p, Power power) {
        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
            setActive(p, power.getTag(), true);
            p.setFlySpeed(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY));
        } else {
            setActive(p, power.getTag(), false);
            p.setFlySpeed(valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }

    @Override
    public String getType() {
        return "apoli:modify_air_speed";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_air_speed;
    }

    public void apply(Player p) {
        if (modify_air_speed.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                    for (Modifier modifier : power.getModifiers()) {
                        Float value = modifier.value();
                        String operation = modifier.operation();
                        BinaryOperator<Float> mathOperator = Utils.getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = mathOperator.apply(valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY), value);
                            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, result);
                        } else {
                            Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for modify_air_speed!");
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
