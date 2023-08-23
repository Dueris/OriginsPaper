package me.dueris.genesismc.factory.powers.OriginsMod.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NightVision extends CraftPower {
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

    @Override
    public String getPowerFile() {
        return "origins:night_vision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return night_vision;
    }
}
