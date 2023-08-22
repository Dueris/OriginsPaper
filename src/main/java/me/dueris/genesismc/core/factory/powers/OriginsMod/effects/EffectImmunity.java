package me.dueris.genesismc.core.factory.powers.OriginsMod.effects;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.effect_immunity;

public class EffectImmunity extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (effect_immunity.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(!conditionExecutor.check("condition", "conditions", p, origin, "origins:effect_immunity", null, p)) return;
                    if (origin.getPowerFileFromType("origins:effect_immunity").getEffect() != null) {
                        PotionEffectType effectType = PotionEffectType.getByName(origin.getPowerFileFromType("origins:effect_immunity").getEffect());
                        if (origin.getPowerFileFromType("origins:effect_immunity").isInverted()) {
                            if (effectType != null) {
                                if (p.hasPotionEffect(effectType)) {
                                    p.removePotionEffect(effectType);
                                }
                            } else {
                                Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.effectImmunity"));
                            }
                        } else {
                            if (effectType != null) {
                                if (!p.hasPotionEffect(effectType)) {
                                    p.addPotionEffect(new PotionEffect(effectType, 25, 1));
                                }
                            } else {
                                Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.effectImmunity"));
                            }
                        }

                    } else if (origin.getPowerFileFromType("origins:effect_immunity").getEffects() != null) {
                        List<String> effectStrings = origin.getPowerFileFromType("origins:effect_immunity").getEffects();
                        for (String effectString : effectStrings) {
                            PotionEffectType effectType = PotionEffectType.getByName(effectString);
                            if (effectType != null) {
                                if (p.hasPotionEffect(effectType)) {
                                    p.removePotionEffect(effectType);
                                }
                            }
                        }
                    } else {
                        Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.effectImmunity"));
                    }
                }
            }
        }
    }
}
