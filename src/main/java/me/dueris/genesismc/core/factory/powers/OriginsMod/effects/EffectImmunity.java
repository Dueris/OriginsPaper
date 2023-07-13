package me.dueris.genesismc.core.factory.powers.OriginsMod.effects;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.effect_immunity;

public class EffectImmunity extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (effect_immunity.contains(p)) {
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    List<String> effectStrings = origin.getPowerFileFromType("origins:effect_immunity").getEffects();
                    for(String effectString : effectStrings){
                        PotionEffectType effectType = PotionEffectType.getByName(effectString);
                        if(effectType != null){
                            if(p.hasPotionEffect(effectType)){
                                p.removePotionEffect(effectType);
                            }
                        }
                    }
                }


            }
        }
    }
}
