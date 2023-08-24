package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.bukkit.Material.AIR;

public class Climbing extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    public ArrayList<Player> active_climbing = new ArrayList<>();

    public boolean isActiveClimbing(Player player) {
        return active_climbing.contains(player);
    }

    public ArrayList<Player> getActiveClimbingMap() {
        return active_climbing;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (climbing.contains(p)) {
                if ((p.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
                        p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid() ||
                        p.getEyeLocation().add(0, 1, 0).getBlock().getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid() ||
                        p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()) && (

                        p.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isCollidable() ||
                                p.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isCollidable() ||
                                p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isCollidable() ||
                                p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isCollidable() ||
                                p.getEyeLocation().add(0, 1, 0).getBlock().getType().isCollidable() ||
                                p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType().isCollidable() ||
                                p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType().isCollidable() ||
                                p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType().isCollidable() ||
                                p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isCollidable()
                )) {
                    Block block = p.getTargetBlock(null, 2);
                    for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                        boolean cancel_bool = origin.getPowerFileFromType("origins:climbing").getRainCancel();
                        ConditionExecutor executor = new ConditionExecutor();
                        if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                            if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                            if (!cancel_bool) {
                                if (!p.isSneaking()) return;
                                p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 2, false, false, false));
                                getActiveClimbingMap().add(p);
                                new BukkitRunnable(){
                                    @Override
                                    public void run() {
                                        getActiveClimbingMap().remove(p);
                                    }
                                }.runTaskLater(GenesisMC.getPlugin(), 1l);
                            } else {
                                if (block.getType() != AIR && p.isSneaking() && !p.isInRain()) {
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 2, false, false, false));
                                    getActiveClimbingMap().add(p);
                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            getActiveClimbingMap().remove(p);
                                        }
                                    }.runTaskLater(GenesisMC.getPlugin(), 1l);
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
    }

    @Override
    public String getPowerFile() {
        return "origins:climbing";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return climbing;
    }
}
