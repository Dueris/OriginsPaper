package me.dueris.genesismc.factory.powers.armour;

import me.dueris.genesismc.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;

import static me.dueris.genesismc.factory.powers.Power.light_armor;
import static org.bukkit.Material.*;

public class GoldAmourBellow extends BukkitRunnable implements Listener {
    public static EnumSet<Material> not_able;

    static {
        not_able = EnumSet.of(DIAMOND_CHESTPLATE, DIAMOND_HELMET, DIAMOND_LEGGINGS, DIAMOND_BOOTS, NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS);
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (light_armor.contains(p)) {
                if (p.getEquipment().getHelmet() != null && not_able.contains(p.getEquipment().getHelmet().getType())) {
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.HEAD);
                }
                if (p.getEquipment().getChestplate() != null && not_able.contains(p.getEquipment().getChestplate().getType())) {
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.CHEST);
                }
                if (p.getEquipment().getLeggings() != null && not_able.contains(p.getEquipment().getLeggings().getType())) {
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.LEGS);
                }
                if (p.getEquipment().getBoots() != null && not_able.contains(p.getEquipment().getBoots().getType())) {
                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.FEET);
                }
            }
        }
    }
}


