package me.dueris.genesismc.core.factory.powers.effects;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.LayerContainer;
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            HashMap<LayerContainer, OriginContainer> origins = OriginPlayer.getOrigin(p);
            Set<LayerContainer> layers = origins.keySet();
            for (LayerContainer layer : layers) {
                if (night_vision.contains(p)) {
                    Long strength = OriginPlayer.getOrigin(p, layer).getPowerFileFromType("origins:night_vision").getStrength();
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, Math.toIntExact(strength), false, false, false));
                }

            }
        }
    }
}
