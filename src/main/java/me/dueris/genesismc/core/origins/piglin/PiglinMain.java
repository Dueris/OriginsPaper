package me.dueris.genesismc.core.origins.piglin;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;

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
}
