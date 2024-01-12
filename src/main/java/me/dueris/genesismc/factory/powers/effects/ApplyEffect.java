package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplyEffect extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {
        if (apply_effect.contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {

                        setActive(p, power.getTag(), true);
                        List<HashMap<String, Object>> effectDataList = power.getEffectData();
                        for (HashMap<String, Object> effectData : effectDataList) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effectData.get("effect").toString().split(":")[1].toUpperCase()), Integer.valueOf(effectData.get("duration").toString()), Integer.valueOf(effectData.get("amplifier").toString())));
                        }
                    } else {

                        setActive(p, power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:apply_effect";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return apply_effect;
    }
}
