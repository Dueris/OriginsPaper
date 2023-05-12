package me.dueris.genesismc.core.factory.powers.block;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.master_of_webs;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.COBWEB;

public class MasterWebs implements Listener {

    @EventHandler
    public void WebMaster(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (master_of_webs.contains(origintag)) {
                Location loc = e.getEntity().getLocation();
                Block b = loc.getBlock();
                Random random = new Random();
                int r = random.nextInt(10);
                if (r == 3) {
                    b.setType(COBWEB);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if(b.getType() == AIR || b.getType() == COBWEB){
                                b.setType(AIR);
                                this.cancel();
                            }else{
                                this.cancel();
                            }
                        }

                    }.runTaskTimer(GenesisMC.getPlugin(), 40L, 5L);
                }
            }
        }
    }

}
