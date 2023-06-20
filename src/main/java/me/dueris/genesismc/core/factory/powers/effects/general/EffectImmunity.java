package me.dueris.genesismc.core.factory.powers.effects.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.effect_immunity;

public class EffectImmunity extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (effect_immunity.contains(p)) {
//                List<String> effectStrings = OriginPlayer.getOrigin(p).getPowerFileFromType("origins:effect_immunity").getEffects();
//                for(String effectString : effectStrings){
//                    PotionEffectType effectType = PotionEffectType.getByName(effectString);
//                    if(effectType != null){
//                        if(p.hasPotionEffect(effectType)){
//                            p.removePotionEffect(effectType);
//                        }
//                    }
//                }

            }
        }
    }
}
