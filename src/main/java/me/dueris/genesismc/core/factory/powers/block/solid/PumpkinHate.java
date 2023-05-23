package me.dueris.genesismc.core.factory.powers.block.solid;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import static me.dueris.genesismc.core.factory.powers.Powers.pumpkin_hate;

public class PumpkinHate implements Listener {

    @EventHandler
    public void OnArmorChange(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        if (pumpkin_hate.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            if (e.getNewItem() == null) return;
            if (e.getNewItem().getType() == Material.CARVED_PUMPKIN) {
                p.getInventory().setHelmet(new ItemStack(Material.AIR));
                p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.CARVED_PUMPKIN));
            }
        }
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e){
        Player p = e.getPlayer();
        if (pumpkin_hate.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            if(e.getItem().getType().equals(Material.PUMPKIN_PIE)){
                p.getWorld().createExplosion(p.getLocation(), 0);
                p.setHealth(1);
                p.setFoodLevel(p.getFoodLevel()-8);
            }
            if(e.getItem().getType().equals(Material.POTION)){
                p.damage(2);
            }
        }

    }

}
