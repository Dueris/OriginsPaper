package me.dueris.genesismc.core.origins.enderian;


import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;

public class EnderSilkTouch implements Listener {
  private static EnumSet<Material> m;
  private static EnumSet<Material> tools;

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    if (!e.getBlock().getType().equals(Material.AIR)) {
      Player p = e.getPlayer();
      Block b = e.getBlock();
      PersistentDataContainer data = p.getPersistentDataContainer();
      int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
      if (originid == 0401065) {
        int ic = 1;
        if (p != null && p.getGameMode().equals(GameMode.SURVIVAL) && p.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
          if (!e.getBlock().getType().isItem()) {
            return;
          }

          if (!m.contains(e.getBlock().getType())) {
            if (e.getBlock().getState() instanceof ShulkerBox) {
              return;
            }
            if (!m.contains(e.getBlock().getType())) {
              if (e.getBlock().getState() instanceof CreatureSpawner) {
                return;
              }


              if (e.getBlock().getType().toString().endsWith("BANNER")) {
                return;
              }

              e.setDropItems(false);
              ItemStack i = new ItemStack(e.getBlock().getType(), 1);

              try {
                p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), i);
                if (ic == 2) {
                  p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), i);
                }
              } catch (Exception var6) {
              }
            }

          }
        }
      }
    }
  }

    static {
      m = EnumSet.of(Material.AIR, Material.REDSTONE_WIRE, Material.REDSTONE_WALL_TORCH, Material.REDSTONE_TORCH, Material.PISTON_HEAD, Material.GRASS, Material.TALL_GRASS, Material.TALL_SEAGRASS, Material.SEA_PICKLE, Material.SEAGRASS, Material.VINE, Material.CHEST, Material.TRAPPED_CHEST, Material.JUKEBOX, Material.FURNACE, Material.HOPPER, Material.DRAGON_HEAD, Material.DRAGON_WALL_HEAD, Material.COMPOSTER, Material.FLETCHING_TABLE, Material.LOOM, Material.BARREL, Material.SMOKER, Material.DROPPER, Material.DISPENSER, Material.SNOW, Material.LECTERN, Material.WHEAT, Material.MELON_STEM, Material.ATTACHED_MELON_STEM, Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.SOUL_TORCH, Material.SOUL_WALL_TORCH, Material.WALL_TORCH, Material.END_PORTAL, Material.NETHER_PORTAL, Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD, Material.STRING, Material.TRIPWIRE, Material.SWEET_BERRY_BUSH, Material.FIRE, Material.SOUL_FIRE, Material.KELP, Material.KELP_PLANT, Material.DRIED_KELP);
      tools = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }
  }

