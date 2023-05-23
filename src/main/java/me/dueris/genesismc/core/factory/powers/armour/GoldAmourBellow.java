package me.dueris.genesismc.core.factory.powers.armour;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.light_armor;
import static org.bukkit.Material.*;

public class GoldAmourBellow implements Listener {
    public static EnumSet<Material> not_able;
    static {
        not_able = EnumSet.of(DIAMOND_CHESTPLATE, DIAMOND_HELMET, DIAMOND_LEGGINGS, DIAMOND_BOOTS, NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS);
    }
    @EventHandler
    public void OnChangeArmour(PlayerArmorChangeEvent e){
        Player p = e.getPlayer();
        if (!light_armor.contains(OriginPlayer.getOriginTag(p))) return;
        if (e.getNewItem() == null) return;

        ArrayList<ItemStack> armour = new ArrayList<>(Arrays.asList(p.getInventory().getArmorContents()));
        for (ItemStack item : armour) {
            if (item == null) continue;
            if (!not_able.contains(item.getType())) continue;
            armour.set(armour.indexOf(item), null);
            p.getInventory().setArmorContents(armour.toArray(new ItemStack[0]));
            p.getInventory().addItem(e.getNewItem());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                ArrayList<ItemStack> armour = new ArrayList<>(Arrays.asList(p.getInventory().getArmorContents()));
                for (ItemStack item : armour) {
                    if (item == null) continue;
                    if (!not_able.contains(item.getType())) continue;
                    armour.set(armour.indexOf(item), null);
                    p.getInventory().setArmorContents(armour.toArray(new ItemStack[0]));
                    p.getInventory().addItem(e.getNewItem());
                }
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 5, 20);

    }
}


