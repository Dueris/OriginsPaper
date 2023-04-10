package me.dueris.genesismc.core.origins.arachnid;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.sun.tools.javac.comp.Check;
import it.unimi.dsi.fastutil.ints.AbstractInt2ReferenceFunction;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

import static org.bukkit.Material.AIR;

public class ArachnidClimb implements Listener {

    @EventHandler
    public void climb(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 1709012) {
            if (p.getLocation().getBlock().getRelative(BlockFace.EAST).getType() != AIR ||
                    p.getLocation().getBlock().getRelative(BlockFace.WEST).getType() != AIR ||
                    p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType() != AIR ||
                    p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType() != AIR ||
                    p.getEyeLocation().add(0, 1, 0).getBlock().getType() != AIR ||
                    p.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType() != AIR ||
                    p.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType() != AIR ||
                    p.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType() != AIR ||
                    p.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType() != AIR) {
                Block block = p.getTargetBlock(null, 2);
                Location bl = block.getLocation();
                if (block.getType() != AIR && p.isSneaking()) {
                    if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("STAIRS")){
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 3, 1, false, false, false));
                }
                }else{
                    if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("stair")) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 2, 3, false, false, false));
                    }
                }
            }
        }
    }
}
