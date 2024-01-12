package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bukkit.Material.AIR;

public class Climbing extends CraftPower {

    public ArrayList<Player> active_climbing = new ArrayList<>();

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

    public boolean isActiveClimbing(Player player) {
        return active_climbing.contains(player);
    }

    public ArrayList<Player> getActiveClimbingMap() {
        return active_climbing;
    }

    @Override
    public void run(Player p) {
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
                for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                    for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        boolean cancel_bool = power.getRainCancel();
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {

                            setActive(p, power.getTag(), true);
                            if (!cancel_bool) {
                                if (!p.isSneaking()) return;
                                p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 2, false, false, false));
                                getActiveClimbingMap().add(p);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        getActiveClimbingMap().remove(p);
                                    }
                                }.runTaskLater(GenesisMC.getPlugin(), 1L);
                            } else {
                                if (block.getType() != AIR && p.isSneaking() && !p.isInRain()) {
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 2, false, false, false));
                                    getActiveClimbingMap().add(p);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            getActiveClimbingMap().remove(p);
                                        }
                                    }.runTaskLater(GenesisMC.getPlugin(), 1L);
                                }
                            }
                        } else {

                            setActive(p, power.getTag(), false);
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
