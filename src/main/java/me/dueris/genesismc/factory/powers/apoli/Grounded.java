package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Grounded extends CraftPower {

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
    public void run(Player player) {
        ArrayList<Location> platform_pos = new ArrayList<>();
        if (grounded.contains(player)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                Location location = player.getLocation();
                Location current_block_platform_pos = location.add(0, -1, 0);
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", player, power, "apoli:grounded", player, null, null, null, player.getInventory().getItemInHand(), null)) {
                        setActive(player, power.getTag(), true);
                        if (current_block_platform_pos.getBlock().getType().equals(Material.AIR)) {
                            platform_pos.add(current_block_platform_pos);
                            CraftPlayer craftPlayer = (CraftPlayer) player;
                            craftPlayer.sendBlockChange(current_block_platform_pos, Material.BARRIER.createBlockData());
                            if (player.isSneaking()) {
                                craftPlayer.sendBlockChange(current_block_platform_pos, current_block_platform_pos.getBlock().getBlockData());
                                if (!current_block_platform_pos.add(0, -1, 0).getBlock().isCollidable()) {
                                    craftPlayer.teleportAsync(current_block_platform_pos.add(0, -1, 0));
                                }
                            }
                        } else {
                            for (Location thing : platform_pos) {
                                Block block = thing.getBlock();
                                CraftPlayer craftPlayer = (CraftPlayer) player;
                                craftPlayer.sendBlockChange(thing, block.getBlockData());
                            }
                        }
                    } else {
                        setActive(player, power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:grounded";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return grounded;
    }


}