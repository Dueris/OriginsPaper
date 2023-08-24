package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class WalkOnFluid extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    HashMap<Player, Location> loc = new HashMap<>();

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(getPowerArray().contains(p)){
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    ConditionExecutor executor = new ConditionExecutor();
                    if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                        if(!p.getLocation().add(0, -1, 0).getBlock().isSolid()){
                            if(p.getLocation().add(0, -1, 0).getBlock().getType() == Material.WATER || p.getLocation().add(0, -1, 0).getBlock().getType() == Material.LAVA){
                                if(p.getLocation().add(0, -1, 0).getBlock().getType().equals(Material.valueOf(origin.getPowerFileFromType(getPowerFile()).get("fluid").toString().toUpperCase().split(":")[1]))){
                                    CraftPlayer craftPlayer = (CraftPlayer) p;
                                    if(p.getLocation().add(0, -1, 0).getBlock().getType() == Material.WATER){
                                        loc.put(p, p.getLocation().add(0, -1, 0).getBlock().getLocation());
                                        craftPlayer.sendBlockChange(p.getLocation().add(0, -1, 0).getBlock().getLocation(), Material.ICE.createBlockData());
                                        for(Location location : loc.values()){
                                            if(location != p.getLocation().add(0, -1, 0).getBlock().getLocation()) loc.remove(p, location);
                                            craftPlayer.sendBlockChange(location, location.getBlock().getBlockData());
                                        }
                                    }else{
                                        loc.put(p, p.getLocation().add(0, -1, 0).getBlock().getLocation());
                                        craftPlayer.sendBlockChange(p.getLocation().add(0, -1, 0).getBlock().getLocation(), Material.OBSIDIAN.createBlockData());
                                        for(Location location : loc.values()){
                                            if(location != p.getLocation().add(0, -1, 0).getBlock().getLocation()) loc.remove(p, location);
                                            craftPlayer.sendBlockChange(location, location.getBlock().getBlockData());
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:walk_on_fluid";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return walk_on_fluid;
    }
}
