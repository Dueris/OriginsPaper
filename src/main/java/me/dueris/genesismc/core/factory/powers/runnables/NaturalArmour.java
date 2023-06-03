package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.natural_armor;

public class NaturalArmour extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p: Bukkit.getOnlinePlayers()){
            if(natural_armor.contains(OriginPlayer.getOrigin(p).getTag())){
                p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8);
            }
        }
    }
}
