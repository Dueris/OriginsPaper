package me.dueris.genesismc.core.factory.powers.entity;


import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.EnumSet;
import java.util.Objects;

import static com.sk89q.worldguard.protection.flags.Flags.BLOCK_BREAK;
import static me.dueris.genesismc.core.GenesisMC.getPlugin;
import static me.dueris.genesismc.core.factory.powers.Powers.silk_touch;
import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

public class SilkTouch implements Listener {
  private static EnumSet<Material> m;
  private static EnumSet<Material> tools;

  public static boolean canPlayerBreakBlocks(Player player, Location location) {
    if (getPlugin().getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
      Block block = location.getBlock();
      return player.hasPermission("worldguard.region.bypass." + block.getWorld().getName())
              || player.hasPermission("worldguard.region.bypass")
              || player.hasPermission("worldguard.region.bypass.*");
    }

    return true; // WorldGuard not found or not enabled
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    if (!e.getBlock().getType().equals(Material.AIR)) {
      Player p = e.getPlayer();
      if (silk_touch.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
        if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")){
          if(!canPlayerBreakBlocks(p, p.getLocation())) p.sendMessage(ChatColor.RED + "You are unable to do that"); return;
        }
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

