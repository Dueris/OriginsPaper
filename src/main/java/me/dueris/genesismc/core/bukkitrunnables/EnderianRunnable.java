package me.dueris.genesismc.core.bukkitrunnables;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftThrownPotion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Material.CARVED_PUMPKIN;
import static org.bukkit.Material.ENDER_PEARL;

public class EnderianRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 0401065) {


                List<Entity> nearby23 = p.getNearbyEntities(3, 3, 3);

                for (Entity tmp : nearby23)
                    if (tmp instanceof CraftThrownPotion)
                        p.damage(2);


                ItemStack infinpearl = new ItemStack(ENDER_PEARL);


                ItemMeta pearl_meta = infinpearl.getItemMeta();
                pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                ArrayList<String> pearl_lore = new ArrayList<>();
                pearl_meta.setUnbreakable(true);
                pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                pearl_meta.setLore(pearl_lore);
                infinpearl.setItemMeta(pearl_meta);
                pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                Random random = new Random();

                int r = random.nextInt(3000);
                if (r == (int) 3 || r == (int) 9 || r == (int) 11 || r == (int) 998 || r == (int) 2279 || r == (int) 989) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 10, 9);
                }

                if(p.getInventory().getItemInMainHand().isSimilar(infinpearl)){
                    if (p.getInventory().getItemInMainHand().getAmount() >= 2) {
                            p.getInventory().getItemInMainHand().setAmount(1);
                    }
                }else if(p.getInventory().getItemInMainHand().getAmount() != 1 && p.getInventory().getItemInMainHand().getAmount() != 0){
                    if(p.getEquipment().getItemInMainHand().equals(infinpearl)) {
                        p.getInventory().getItemInMainHand().setAmount(1);
                    }
                }
            }

        }
    }

    }

