package me.dueris.genesismc.core.factory.powers.item;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.gold_item_buff;
import static org.bukkit.Material.*;

public class GoldItemBuff implements Listener {
    public static EnumSet<Material> goldenTools;

    static {
        goldenTools = EnumSet.of(GOLDEN_AXE, GOLDEN_HOE, GOLDEN_PICKAXE, GOLDEN_SWORD, GOLDEN_SHOVEL);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        if (gold_item_buff.contains(p.getUniqueId().toString())) {
            if (goldenTools.contains(p.getInventory().getItemInMainHand().getType())) {
                e.setDamage(e.getDamage() * 1.25);
            }
        }
    }

}
