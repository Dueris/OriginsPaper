package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.factory.powers.effects.StackingStatusEffect.getPotionEffectType;

public class EffectImmunity extends CraftPower {

    public EffectImmunity() {

    }

    @Override
    public void run(Player p) {
        if (effect_immunity.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        setActive(power.getTag(), true);
                        if (!power.getEffects().isEmpty()) {
                            List<String> effectStrings = power.getEffects();
                            for (String effectString : effectStrings) {
                                PotionEffectType effectType = getPotionEffectType(effectString);
                                if (effectType != null) {
                                    if (p.hasPotionEffect(effectType)) {
                                        p.removePotionEffect(effectType);
                                    }
                                }
                            }
                        } else {
                            Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.effectImmunity"));
                        }
                    } else {
                        setActive(power.getTag(), false);
                    }
                }

            }
        }
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
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
