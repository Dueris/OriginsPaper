package me.dueris.genesismc.core.factory.powers.effects;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Set;

import static me.dueris.genesismc.core.factory.powers.Powers.night_vision;

public class NightVision extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()){
            HashMap<String, OriginContainer> origins = OriginPlayer.getOrigin(p);
            Set<String> layers = origins.keySet();
            for (String layer : layers) {
                if(night_vision.contains(p)){
                    int strength = Integer.parseInt(OriginPlayer.getOrigin(p, layer).getPowerFileFromType("origins:night_vision").getValue("strength"));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20, strength, false, false, false));
                }

            }
        }
    }
}
