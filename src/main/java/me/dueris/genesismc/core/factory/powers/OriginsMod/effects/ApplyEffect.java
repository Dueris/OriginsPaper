package me.dueris.genesismc.core.factory.powers.OriginsMod.effects;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.apply_effect;

public class ApplyEffect extends BukkitRunnable implements Listener {
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
}
