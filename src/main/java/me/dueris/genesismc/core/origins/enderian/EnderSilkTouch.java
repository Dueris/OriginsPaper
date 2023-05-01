package me.dueris.genesismc.core.origins.enderian;


import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.ChatColor;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import static org.bukkit.Bukkit.getServer;

public class EnderSilkTouch implements Listener {
  private static EnumSet<Material> m;
  private static EnumSet<Material> tools;

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    if (!e.getBlock().getType().equals(Material.AIR)) {
      Player p = e.getPlayer();
      PersistentDataContainer data = p.getPersistentDataContainer();
      @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
      if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
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
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "Send to Dueris: Error with Enderian Silk Touch");

              }
            }

          }
        }
      }
    }
  }

    static {
      m = EnumSet.of(Material.PISTON_HEAD, Material.VINE, Material.WHEAT, Material.MELON_STEM, Material.ATTACHED_MELON_STEM, Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.END_PORTAL, Material.NETHER_PORTAL, Material.FIRE, Material.SOUL_FIRE);
      tools = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }
  }

