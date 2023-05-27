package me.dueris.genesismc.core.factory.powers.block.solid;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.dueris.genesismc.core.GenesisMC.getPlugin;
import static me.dueris.genesismc.core.factory.powers.Powers.pumpkin_hate;
import static org.bukkit.Bukkit.getServer;

public class PumpkinHate extends BukkitRunnable implements Listener {
    public static HashMap invisiblePlayers = new HashMap<>();

/*
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
    */

    public static void makePlayerInvisible(Player player, Player target) {
        invisiblePlayers.put(player, target);
        player.hidePlayer(getPlugin(), target);
    }

    public static void makePlayerVisible(Player player) {
        Player target = (Player) invisiblePlayers.remove(player);
        if (target != null) {
            player.showPlayer(getPlugin(), target);
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

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if (pumpkin_hate.contains(OriginPlayer.getOriginTag(p))) {
                if(p.getEquipment().getHelmet() == null) return;
                if(p.getEquipment().getHelmet().getType().equals(Material.CARVED_PUMPKIN)){
                    makePlayerInvisible(p, p);
                }else{
                    makePlayerVisible(p);
                }
            }
        }
    }
}
