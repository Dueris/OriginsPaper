package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class WalkOnFluid extends CraftPower {

    HashMap<Player, Location> loc = new HashMap<>();

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
        if (getPowerArray().contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
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
