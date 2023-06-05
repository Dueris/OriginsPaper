package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.natural_armor;
import static me.dueris.genesismc.core.factory.powers.Powers.nine_lives;

public class AttributeHandler extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p: Bukkit.getOnlinePlayers()){
            if(natural_armor.contains(p)){
                p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8);
            }
            if(nine_lives.contains(p)){
                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
            }
        }
    }
}
