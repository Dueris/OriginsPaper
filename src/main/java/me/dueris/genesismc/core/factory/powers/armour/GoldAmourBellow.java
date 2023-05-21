package me.dueris.genesismc.core.factory.powers.armour;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
        if(light_armor.contains(OriginPlayer.getOriginTag(p))) {
            if(not_able.contains(e.getNewItem().getType())){
                /*e.getNewItem().setType(e.getOldItem().getType());
                e.getNewItem().setItemMeta(e.getOldItem().getItemMeta());
                e.getPlayer().getInventory().addItem(e.getNewItem());
                e.getPlayer().getItemOnCursor().setAmount(0);

                if(e.getOldItem() == null){
                    e.getNewItem().setAmount(0);
                    p.getInventory().addItem(e.getNewItem());
                    p.getItemOnCursor().setAmount(0);
                }

                p.sendMessage("fjhsldkhjhfsdfsdfsdfsdfsd");

                 */
            }

        }
    }

}
