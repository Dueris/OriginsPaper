package me.dueris.genesismc.factory.powers.OriginsMod.effects;

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
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (apply_effect.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", p, origin, "origins:apply_effect", null, p)) {
                        List<HashMap<String, Object>> effectDataList = origin.getPowerFileFromType("origins:apply_effect").getEffectData();
                        for (HashMap<String, Object> effectData : effectDataList) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effectData.get("effect").toString().split(":")[1].toUpperCase()), Integer.valueOf(effectData.get("duration").toString()), Integer.valueOf(effectData.get("amplifier").toString())));
                        }
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
