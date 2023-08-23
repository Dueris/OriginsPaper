package me.dueris.genesismc.factory.powers.OriginsMod.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.translation.LangConfig;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EffectImmunity extends CraftPower {
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
                                Bukkit.getLogger().warning(LangConfig.getLocalizedString("powers.errors.effectImmunity"));
                            }
                        } else {
                            if (effectType != null) {
                                if (!p.hasPotionEffect(effectType)) {
                                    p.addPotionEffect(new PotionEffect(effectType, 25, 1));
                                }
                            } else {
                                Bukkit.getLogger().warning(LangConfig.getLocalizedString("powers.errors.effectImmunity"));
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
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString("powers.errors.effectImmunity"));
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:effect_immunity";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return effect_immunity;
    }
}
