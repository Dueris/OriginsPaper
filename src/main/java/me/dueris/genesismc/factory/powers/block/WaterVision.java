package me.dueris.genesismc.factory.powers.block;

import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.block.WaterBreathe.isInBreathableWater;

public class WaterVision extends CraftPower {

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
    public void run(Player p) {
        if (water_vision.contains(p)) {
            if (isInBreathableWater(p)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 15, 3, false, false));
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:water_vision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return water_vision;
    }
}
