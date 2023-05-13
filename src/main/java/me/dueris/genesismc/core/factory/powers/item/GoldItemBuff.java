package me.dueris.genesismc.core.factory.powers.item;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.gold_item_buff;
import static org.bukkit.Material.*;
import static org.bukkit.Material.SPIDER_EYE;

public class GoldItemBuff implements Listener {
    public static EnumSet<Material> goldenTools;
    static {
        goldenTools = EnumSet.of(GOLDEN_AXE, GOLDEN_HOE, GOLDEN_PICKAXE, GOLDEN_SWORD, GOLDEN_SHOVEL);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (gold_item_buff.contains(origintag)) {
            if (goldenTools.contains(p.getInventory().getItemInMainHand().getType())) {
                e.setDamage(e.getDamage()*1.25);
            }
        }
    }

}
