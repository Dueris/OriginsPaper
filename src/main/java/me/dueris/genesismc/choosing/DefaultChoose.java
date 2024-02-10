package me.dueris.genesismc.choosing;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChooseEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.items.OrbOfOrigins;
import me.dueris.genesismc.utils.SendCharts;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static me.dueris.genesismc.items.OrbOfOrigins.orb;
import static org.bukkit.Bukkit.getServer;

public class DefaultChoose {

    public static void DefaultChoose(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);

        //default choose
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 2);
        p.spawnParticle(Particle.CLOUD, p.getLocation(), 100);
        p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 6);
        p.setCustomNameVisible(false);
        p.setHealthScaled(false);

        OriginChooseEvent chooseEvent = new OriginChooseEvent(p);
        getServer().getPluginManager().callEvent(chooseEvent);

        SendCharts.originPopularity(p);

    }
}
