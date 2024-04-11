package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class WalkOnFluid extends CraftPower {

    HashMap<Player, Location> loc = new HashMap<>();


    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                        setActive(p, power.getTag(), true);
                        if (!p.getLocation().add(0, -1, 0).getBlock().isSolid()) {
                            if (p.getLocation().add(0, -1, 0).getBlock().getType() == Material.WATER || p.getLocation().add(0, -1, 0).getBlock().getType() == Material.LAVA) {
                                if (p.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.valueOf(power.getString("fluid").toUpperCase().split(":")[1]))) {
                                    CraftPlayer craftPlayer = (CraftPlayer) p;
                                    if (p.getLocation().add(0, -1, 0).getBlock().getType() == Material.WATER) {
                                        loc.put(p, p.getLocation().add(0, -1, 0).getBlock().getLocation());
                                        craftPlayer.sendBlockChange(p.getLocation().add(0, -1, 0).getBlock().getLocation(), Material.ICE.createBlockData());
                                        for (Location location : loc.values()) {
                                            if (location != p.getLocation().add(0, -1, 0).getBlock().getLocation())
                                                loc.remove(p, location);
                                            craftPlayer.sendBlockChange(location, location.getBlock().getBlockData());
                                        }
                                    } else {
                                        loc.put(p, p.getLocation().add(0, -1, 0).getBlock().getLocation());
                                        craftPlayer.sendBlockChange(p.getLocation().add(0, -1, 0).getBlock().getLocation(), Material.OBSIDIAN.createBlockData());
                                        for (Location location : loc.values()) {
                                            if (location != p.getLocation().add(0, -1, 0).getBlock().getLocation())
                                                loc.remove(p, location);
                                            craftPlayer.sendBlockChange(location, location.getBlock().getBlockData());
                                        }
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
    public String getPowerFile() {
        return "apoli:walk_on_fluid";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return walk_on_fluid;
    }
}
