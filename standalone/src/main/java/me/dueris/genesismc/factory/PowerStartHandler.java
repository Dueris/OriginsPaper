package me.dueris.genesismc.factory;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static me.dueris.genesismc.factory.powers.CraftPower.findCraftPowerClasses;

public class PowerStartHandler {

    public static void startPowers(Player player) throws Exception {
        Bukkit.getPluginManager().registerEvents(new AttributeHandler.Reach(), GenesisMC.getPlugin());
        List<Class<? extends CraftPower>> craftPowerClasses = findCraftPowerClasses();
        for (Class<? extends CraftPower> c : craftPowerClasses) {
            if (CraftPower.class.isAssignableFrom(c)) {
                CraftPower instance = c.newInstance();
//                if(instance.getPowerArray().contains(originScheduler.getPlayer())){
//                    originScheduler.runTaskTimer(new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            instance.run(originScheduler.getPlayer());
//                        }
//                    }, 0, 1);
//                    if(instance instanceof Listener || Listener.class.isAssignableFrom(instance.getClass())){
//                        Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
//                    }
//                }
            }
        }
    }
}
