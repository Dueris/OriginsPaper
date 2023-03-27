package me.purplewolfmc.genesismc.origins.shulker;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Random;

import static org.bukkit.Material.*;

public class ShulkerMain implements Listener {

  @EventHandler
  public void onSprint(PlayerMoveEvent e){
    Player p = e.getPlayer();
    if(p.isSprinting()){
      if(p.getScoreboardTags().contains("shulker")) {
        Random random = new Random();
        int r = random.nextInt(1000);
        if (r == (int) 998 || r == (int) 132 || r == (int) 989 || r == (int) 929 || r == (int) 459 || r == (int) 29 || r == (int) 812) {
          int foodamt = p.getFoodLevel();
          p.setFoodLevel(foodamt - 1);
        }
      }
    }
  }

  @EventHandler
  public void onMoveShulk(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    float walk = 0.185F;
    if (p.getScoreboardTags().contains("shulker")) {
      p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8);
      p.setWalkSpeed((float) walk);
      p.setHealthScale(20);

    }
  }

  public static EnumSet<Material> nat_stones;
  public static EnumSet<Material> tool;

  @EventHandler
  public void onBreakShulk(BlockBreakEvent e) {
    Collection<ItemStack> drops = e.getBlock().getDrops();
    Collection drope = e.getBlock().getDrops();
    Player p = e.getPlayer();
    ItemStack i = new ItemStack(e.getBlock().getType(), 1);

    if (p.getScoreboardTags().contains("shulker")) {
      if (!nat_stones.contains(e.getBlock().getType())) {
          //do nothing
      }else {
        if (p != null && p.getGameMode().equals(GameMode.SURVIVAL) && !tool.contains(p.getInventory().getItemInMainHand())) {
          if(e.getBlock().getType().equals(STONE)){
            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(COBBLESTONE));
          } else if (e.getBlock().getType().toString().contains("coal") && e.getBlock().getType().toString().contains("ore")) {
            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(COAL));
          } else if (e.getBlock().getType().equals(DEEPSLATE)) {
            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(COBBLED_DEEPSLATE));
          } else {
            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), i);
          }

        }
      }
    }
  }

  @EventHandler
  public void onhitShulk(EntityDamageEvent e){
    if(e.getEntity().getScoreboardTags().contains("shulker")){
      e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_SHULKER_HURT, 10.0F, 5.0F);
    }
  }
  
  @EventHandler
  public void onDeathShulk(EntityDeathEvent e){
    if(e.getEntity().getScoreboardTags().contains("shulker")) {
      e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_SHULKER_DEATH, 10.0F, 5.0F);
      Random random = new Random();
      int r = random.nextInt(1000);
      if (r == 8) {
        e.getEntity().getLocation().getWorld().dropItem(e.getEntity().getLocation(), new ItemStack(SHULKER_SHELL, 1));
      }
    }
  }

  static {
    nat_stones = EnumSet.of(TUFF, STONE, ANDESITE, BLACKSTONE, COBBLESTONE, CALCITE, AMETHYST_BLOCK, ANDESITE_SLAB, ANDESITE_STAIRS, ANDESITE_WALL, DIORITE, DIORITE_SLAB, DIORITE_STAIRS, DIORITE_WALL, BLACKSTONE_SLAB, BLACKSTONE_STAIRS, BLACKSTONE_WALL, COAL_ORE, DEEPSLATE, DEEPSLATE_COAL_ORE, NETHERRACK, END_STONE);
    tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
  }
}

