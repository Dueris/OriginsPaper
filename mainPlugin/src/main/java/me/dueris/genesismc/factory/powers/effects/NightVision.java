package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NightVision extends CraftPower {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    public NightVision() {

    }

    @Override
    public void run(Player p) {
        HashMap<LayerContainer, OriginContainer> origins = OriginPlayer.getOrigin(p);
        Set<LayerContainer> layers = origins.keySet();
        for (LayerContainer layer : layers) {
            if (night_vision.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                            if (power == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(power.getTag(), true);
                            Double strength = power.getStrength();
                            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, roundNumber(power.getStrength()), false, false, false));
                        } else {
                            if (power == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(power.getTag(), false);
                        }
                    }
                }
            }

        }
    }

    public int roundNumber(double num) {
        if (String.valueOf(num).contains(".")) {
            String[] parts = String.valueOf(num).split("\\.");
            if (parts.length > 1) {
                int decimalPart = Integer.parseInt(parts[1]);
                if (decimalPart >= 5) {
                    return Integer.parseInt(parts[0]) + 1;
                } else {
                    return Integer.parseInt(parts[0]);
                }
            }
        }
        return 0;
    }


    @Override
    public String getPowerFile() {
        return "origins:night_vision";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return night_vision;
    }
}
