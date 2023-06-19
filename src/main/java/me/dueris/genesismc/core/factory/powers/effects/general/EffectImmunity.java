package me.dueris.genesismc.core.factory.powers.effects.general;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BinaryOperator;

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
