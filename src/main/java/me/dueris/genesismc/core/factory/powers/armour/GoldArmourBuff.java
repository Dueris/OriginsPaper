package me.dueris.genesismc.core.factory.powers.armour;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.gold_item_buff;
import static org.bukkit.Material.*;
import static org.bukkit.Material.GOLDEN_LEGGINGS;

public class GoldArmourBuff implements Listener {

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (gold_item_buff.contains(origintag)) {
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
