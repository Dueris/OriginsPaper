package me.dueris.genesismc.core.factory.powers.armour;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.light_armor;
import static org.bukkit.Material.*;

public class GoldAmourBellow extends BukkitRunnable implements Listener {
    public static EnumSet<Material> not_able;
    static {
        not_able = EnumSet.of(DIAMOND_CHESTPLATE, DIAMOND_HELMET, DIAMOND_LEGGINGS, DIAMOND_BOOTS, NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS);
    }
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(light_armor.contains(OriginPlayer.getOriginTag(p))){
                if(p.getEquipment().getHelmet() != null && not_able.contains(p.getEquipment().getHelmet().getType())){
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.HEAD);
                }
                if(p.getEquipment().getChestplate() != null && not_able.contains(p.getEquipment().getChestplate().getType())){
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.CHEST);
                }
                if(p.getEquipment().getLeggings() != null && not_able.contains(p.getEquipment().getLeggings().getType())){
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.LEGS);
                }
                if(p.getEquipment().getBoots() != null && not_able.contains(p.getEquipment().getBoots().getType())){
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.FEET);
                }
            }
        }
    }
}


