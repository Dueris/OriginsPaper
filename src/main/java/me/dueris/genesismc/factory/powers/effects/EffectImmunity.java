package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.factory.powers.effects.StackingStatusEffect.getPotionEffectType;

public class EffectImmunity extends CraftPower {

    public EffectImmunity() {

    }

    @Override
    public void run(Player p) {
        if (effect_immunity.contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        setActive(p, power.getTag(), true);
                        if (!power.getStringList("effects").isEmpty()) {
                            List<String> effectStrings = power.getStringList("effects");
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
                        setActive(p, power.getTag(), false);
                    }
                }

            }
        }
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
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
