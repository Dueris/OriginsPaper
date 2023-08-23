package me.dueris.genesismc.factory.powers.armour;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static me.dueris.genesismc.factory.powers.Power.gold_item_buff;
import static org.bukkit.Material.*;

public class GoldArmourBuff implements Listener {

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        if (gold_item_buff.contains(e.getPlayer())) {
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

}
