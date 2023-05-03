package me.dueris.genesismc.core.origins.shulk;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import static org.bukkit.Material.*;
public class ShulkMain implements Listener {

  public static EnumSet<Material> tools;
  public static EnumSet<Material> nat_stones;

  static {
    nat_stones = EnumSet.of(GRANITE, COBBLED_DEEPSLATE, TUFF, STONE, ANDESITE, BLACKSTONE, COBBLESTONE, CALCITE, AMETHYST_BLOCK, ANDESITE_SLAB,
            ANDESITE_STAIRS, ANDESITE_WALL, DIORITE, DIORITE_SLAB, DIORITE_STAIRS, DIORITE_WALL, BLACKSTONE_SLAB, BLACKSTONE_STAIRS, BLACKSTONE_WALL,
            COAL_ORE, DEEPSLATE, DEEPSLATE_COAL_ORE, NETHERRACK, END_STONE);
      tools = EnumSet.of(
              WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE,
              WOODEN_AXE, STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE,
              WOODEN_SHOVEL, STONE_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL,
              SHEARS);
  }

  @EventHandler
  public void onSprint(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    PersistentDataContainer data = p.getPersistentDataContainer();
    @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
    if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
      if (p.isSprinting() && !p.getGameMode().equals(GameMode.CREATIVE) && !p.getGameMode().equals(GameMode.SPECTATOR)) {
        Random random = new Random();
        int r = random.nextInt(750);
        if(!p.isSwimming()) {
          if (r < 10) {
            int foodamt = p.getFoodLevel();
            p.setFoodLevel(foodamt - 1);
          }
        }
      }
    }
  }


  @EventHandler
  public void onBreakShulk(BlockBreakEvent e) {
    Player p = e.getPlayer();
    ItemStack i = new ItemStack(e.getBlock().getType(), 1);
    PersistentDataContainer data = p.getPersistentDataContainer();
    @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
    if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
      if (nat_stones.contains(e.getBlock().getType())) {
        if (!tools.contains(p.getEquipment().getItemInMainHand().getType())) {
          if (!p.getGameMode().equals(GameMode.CREATIVE)) {
            if (e.getBlock().getType().equals(STONE)) {
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
  }

  @EventHandler
  public void onhitshulkEntity(EntityDamageByEntityEvent e){
    if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
      PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
      @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
      if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
        Player p = (Player) e.getEntity();
        Random random = new Random();

        int r = random.nextInt(10);
        if (r == 8) {
          ShulkerBullet shulkerbullet = p.getWorld().spawn(p.getEyeLocation(), ShulkerBullet.class);

          shulkerbullet.setShooter(p);

          shulkerbullet.setVelocity(p.getLocation().getDirection().multiply(1.4));

          int xrange = 100; //CONFIG

          int yrange = 128; //CONFIG

          int zrange = 100; //CONFIG

          List<Entity> entities = p.getNearbyEntities(xrange, yrange, zrange);

          for (Entity ent : entities) {

            shulkerbullet.setTarget(ent);

            if(shulkerbullet.getTarget() != null) {

              if(ent.getLocation().distance(p.getLocation()) < shulkerbullet.getTarget().getLocation().distance(p.getLocation())) {

                shulkerbullet.setTarget(ent);

              }

            }

          }
        }
      }
    }
  }

  @EventHandler
  public void onhitShulk(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
    PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
      @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
      if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_SHULKER_HURT, 10.0F, 5.0F);
        Random random = new Random();

        int r = random.nextInt(10);
        if (r == 8) {
          e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.SHULKER_BULLET);

        }
      }
    }
  }

  @EventHandler
  public void onDeathShulk(EntityDeathEvent e) {
    if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
      PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
      @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
      if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_SHULKER_DEATH, 10.0F, 5.0F);
        Random random = new Random();
        int r = random.nextInt(100);
        if (r <= 8) {
          e.getEntity().getLocation().getWorld().dropItem(e.getEntity().getLocation(), new ItemStack(SHULKER_SHELL, 1));
        }
      }
    }
  }


  @EventHandler
  public void onTargetShulk(EntityTargetEvent e){
    if(e.getEntity() instanceof ShulkerBullet){
      PersistentDataContainer data = e.getTarget().getPersistentDataContainer();
      @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
      if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
        if(e.getTarget() instanceof Player){
          e.setCancelled(true);
        }
      }
    }
  }
}

