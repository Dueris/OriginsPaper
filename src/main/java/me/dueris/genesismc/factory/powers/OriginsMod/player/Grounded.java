package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Grounded extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @Override
    public void run() {
        ArrayList<Location> platform_pos = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (grounded.contains(player)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                    Location location = player.getLocation();
                    Location current_block_platform_pos = location.add(0, -1, 0);
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", player, origin, "origins:grounded", null, player)) {
                        if(!getPowerArray().contains(player)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
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
                    }else{
                        if(!getPowerArray().contains(player)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:grounded";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return grounded;
    }


}