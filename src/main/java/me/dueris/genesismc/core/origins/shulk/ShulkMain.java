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
import java.util.*;
import static org.bukkit.Material.*;
public class ShulkMain implements Listener {

  @EventHandler
  public void onSprint(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    PersistentDataContainer data = p.getPersistentDataContainer();
    int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
    if (originid == 6503044) {
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
  public void OnUseShield(PlayerInteractEvent e){
    Player p = e.getPlayer();
    PersistentDataContainer data = p.getPersistentDataContainer();
    int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
    if (originid == 6503044) {
      if(e.getItem() != null){
        if(e.getItem().getType().equals(SHIELD)){
          e.setCancelled(true);
        }
      }
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
    PersistentDataContainer data = p.getPersistentDataContainer();
    int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
    if (originid == 6503044) {
      if (nat_stones.contains(e.getBlock().getType())) {
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

  @EventHandler
  public void onhitshulkEntity(EntityDamageByEntityEvent e){
    if (e.getEntity() instanceof Player || e.getEntity() instanceof HumanEntity) {
      PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
      int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
      if (originid == 6503044) {
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
      int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
      if (originid == 6503044) {
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
      int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
      if (originid == 6503044) {
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
      int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
      if (originid == 6503044) {
        if(e.getTarget() instanceof Player){
          Player p = (Player) e.getTarget();
          e.setCancelled(true);
        }
      }
    }
  }

    static {
      nat_stones = EnumSet.of(GRANITE, COBBLED_DEEPSLATE, TUFF, STONE, ANDESITE, BLACKSTONE, COBBLESTONE, CALCITE, AMETHYST_BLOCK, ANDESITE_SLAB, ANDESITE_STAIRS, ANDESITE_WALL, DIORITE, DIORITE_SLAB, DIORITE_STAIRS, DIORITE_WALL, BLACKSTONE_SLAB, BLACKSTONE_STAIRS, BLACKSTONE_WALL, COAL_ORE, DEEPSLATE, DEEPSLATE_COAL_ORE, NETHERRACK, END_STONE);
      tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }
  }

