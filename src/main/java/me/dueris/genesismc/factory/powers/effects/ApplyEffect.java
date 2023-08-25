package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApplyEffect extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (apply_effect.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", p, origin, getPowerFile(), null, p)) {
                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                        List<HashMap<String, Object>> effectDataList = origin.getPowerFileFromType("origins:apply_effect").getEffectData();
                        for (HashMap<String, Object> effectData : effectDataList) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effectData.get("effect").toString().split(":")[1].toUpperCase()), Integer.valueOf(effectData.get("duration").toString()), Integer.valueOf(effectData.get("amplifier").toString())));
                        }
                    }else{
                        if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
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
