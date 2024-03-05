package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsFloat;

public class ModifyVelocityPower extends CraftPower implements Listener {

    @Override
    public void run(Player p) {
        // do nothing
    }

    @EventHandler
    public void velcotiyWEEEEEEEE(PlayerVelocityEvent e) {
        if (getPowerArray().contains(e.getPlayer())) {
            Player p = e.getPlayer();
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                        List<String> identifiers = power.getJsonArray("axes");
                        if (identifiers.isEmpty()) {
                            identifiers.add("x");
                            identifiers.add("y");
                            identifiers.add("z");
                        }
                        Vector vel = e.getVelocity();
                        for (HashMap<String, Object> modifier : power.getJsonListSingularPlural("modifier", "modifiers")) {
                            Float value = Float.valueOf(modifier.get("value").toString());
                            String operation = modifier.get("operation").toString();
                            BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                            for (String axis : identifiers) {
                                if (axis == "x") {
                                    vel.setX((float) mathOperator.apply(vel.getX(), value));
                                }
                                if (axis == "y") {
                                    vel.setY((float) mathOperator.apply(vel.getY(), value));
                                }
                                if (axis == "z") {
                                    vel.setZ((float) mathOperator.apply(vel.getZ(), value));
                                }
                            }
                        }
                        setActive(p, power.getTag(), true);
                        e.setVelocity(vel);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                setActive(p, power.getTag(), false);
                            }

                        }.runTaskLater(GenesisMC.getPlugin(), 1);
                    }

                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_velocity";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return ValueModifyingSuperClass.modify_velocity;
    }

}
