package me.dueris.genesismc.core.origins.piglin;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.core.GenesisMC;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import static org.bukkit.Material.*;

public class PiglinMain implements Listener {

    ArrayList<Integer> piglinsHit;
    public static EnumSet<Material> goldenTools;
    public static EnumSet<Material> edibleFoodPiglin;
    static {
        goldenTools = EnumSet.of(GOLDEN_AXE, GOLDEN_HOE, GOLDEN_PICKAXE, GOLDEN_SWORD, GOLDEN_SHOVEL);
        edibleFoodPiglin = EnumSet.of(
                COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON, COOKED_COD, COOKED_PORKCHOP, COOKED_RABBIT, COOKED_SALMON,
                BEEF, CHICKEN, MUTTON, COD, PORKCHOP, RABBIT, SALMON,
                TROPICAL_FISH, PUFFERFISH, RABBIT_STEW, ROTTEN_FLESH, SPIDER_EYE);
    }

    @EventHandler
    public void onItemConsume(PlayerInteractEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-piglin")) {
            ItemStack item = e.getItem();
            if (item == null) return;
            if (!item.getType().isEdible()) return;
            for (Material food : edibleFoodPiglin) {
                if (item.getType() == food) {
                    return;
                }
            }
            e.setCancelled(true);
        }
    }

    //I like to be SHINY: Golden tools deal extra damage and gold armour has more protection
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-piglin")) {
            if (goldenTools.contains(p.getInventory().getItemInMainHand().getType())) {
                e.setDamage(e.getDamage()*1.25);
            }
        }
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-piglin")) {
            p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
            for (ItemStack armour : p.getInventory().getArmorContents()) {
                if (armour == null) continue;
                if (armour.getType() == GOLDEN_HELMET || armour.getType() == GOLDEN_BOOTS) {
                    p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(p.getAttribute(Attribute.GENERIC_ARMOR).getBaseValue() + 1);
                }
                if (armour.getType() == GOLDEN_CHESTPLATE || armour.getType() == GOLDEN_LEGGINGS) {
                    p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(p.getAttribute(Attribute.GENERIC_ARMOR).getBaseValue() + 2);
                }
            }
        }
    }


    //Friendly Frenemies: Piglins won't attack you unless provoked

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof Player)) return;
        Player p = (Player) e.getTarget();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-piglin")) {
            if (e.getEntity().getType() == EntityType.PIGLIN) {
                if (!piglinsHit.contains(e.getEntity().getEntityId())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPiglinHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        Player p = (Player) e.getDamager();
        LivingEntity entity = (LivingEntity) e.getEntity();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-piglin")) {
            if (e.getEntity().getType() == EntityType.PIGLIN) {
                if (piglinsHit.contains(e.getEntity().getEntityId())) return;
                if (entity.getHealth() - e.getFinalDamage() <= 0) return;
                piglinsHit.add(e.getEntity().getEntityId());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-piglin")) {
            if (!(e.isBedSpawn() || e.isAnchorSpawn())) {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NETHER) {

                        Random random = new Random();
                        Location location = new Location(world, random.nextInt(-300, 300), 32, random.nextInt(-300, 300));

                        respawnLocationSearch:
                        for (int x = (int) (location.getX()-100); x < location.getX()+100; x++) {
                            for (int z = (int) (location.getZ()-100); z < location.getZ()+100; z++) {
                                yLoop:
                                for (int y = (int) (location.getY()); y < location.getY()+68; y++) {
                                    if (new Location(world, x, y, z).getBlock().getType() != AIR) continue;
                                    if (new Location(world, x, y+1, z).getBlock().getType() != AIR) continue;
                                    Material blockBeneath = new Location(world, x, y-1, z).getBlock().getType();
                                    if (blockBeneath == AIR || blockBeneath == LAVA || blockBeneath == FIRE || blockBeneath == SOUL_FIRE) continue;

                                    for (int potentialX = (int) (new Location(world, x, y, z).getX()-2); potentialX < new Location(world, x, y, z).getX()+2; potentialX++) {
                                        for (int potentialY = (int) (new Location(world, x, y, z).getY()); potentialY < new Location(world, x, y, z).getY()+2; potentialY++) {
                                            for (int potentialZ = (int) (new Location(world, x, y, z).getZ()-2); potentialZ < new Location(world, x, y, z).getZ()+2; potentialZ++) {
                                                if (new Location(world, potentialX, potentialY, potentialZ).getBlock().getType() != AIR) continue yLoop;
                                            }
                                        }
                                    }
                                    e.setRespawnLocation(new Location(world, x+0.5, y, z+0.5));
                                    break respawnLocationSearch;
                                }
                            }
                        }
                        break;
                    }
                }
            }

        }
    }
}
