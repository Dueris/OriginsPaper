package me.dueris.genesismc.core.factory.powers.block.fluid;

import net.minecraft.world.item.PotionItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class AirFromPotions implements Listener {
    @EventHandler
    public void OnDrink(PlayerItemConsumeEvent e){
        if(e.getItem().getType().equals(Material.POTION)){
            if(e.getPlayer().getRemainingAir() > 250){e.getPlayer().setRemainingAir(300);}else{
                e.getPlayer().setRemainingAir(e.getPlayer().getRemainingAir() + 50);
            }
        }
    }
}
