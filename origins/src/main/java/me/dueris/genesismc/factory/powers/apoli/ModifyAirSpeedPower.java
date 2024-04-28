package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyAirSpeedPower extends CraftPower {
    private static final HashMap<Player, Float> base = new HashMap<>();

    public static void compute(Player p, Power power) {
        float b = base.containsKey(p) ? base.get(p) : p.getFlySpeed();
        power.getModifiers().forEach(modifier -> p.setFlySpeed(Utils.getOperationMappingsFloat().get(modifier.operation()).apply(b, modifier.value())));
        base.put(p, b);
    }

    @Override
    public void run(Player p, Power power) {
        if (Bukkit.getCurrentTick() % 10 == 0) {
            if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                setActive(p, power.getTag(), false);
                p.setFlySpeed(base.get(p));
                return;
            }
            setActive(p, power.getTag(), true);
            compute(p, power);
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
}
