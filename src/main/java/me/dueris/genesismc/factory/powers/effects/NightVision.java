package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
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

    Player p;

    public NightVision(){
        this.p = p;
    }

    @Override
    public void run(Player p) {
        HashMap<LayerContainer, OriginContainer> origins = OriginPlayer.getOrigin(p);
        Set<LayerContainer> layers = origins.keySet();
        for (LayerContainer layer : layers) {
            if (night_vision.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ConditionExecutor executor = new ConditionExecutor();
                    if (executor.check("condition", "conditions", p, origin, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        if (origin.getPowerFileFromType(getPowerFile()) == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                        Float strength = OriginPlayer.getOrigin(p, layer).getPowerFileFromType("origins:night_vision").getStrength();
                        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, roundNumber(origin.getPowerFileFromType(getPowerFile()).getStrength()), false, false, false));
                    } else {
                        if (origin.getPowerFileFromType(getPowerFile()) == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                }
            }

        }
    }

    public int roundNumber(float num) {
        if (String.valueOf(num).contains(".")) {
            if (Integer.valueOf(String.valueOf(num).split(".")[1]) >= 5) {
                return Integer.valueOf(String.valueOf(num).split(".")[0]) + 1;
            } else {
                return Integer.valueOf(String.valueOf(num).split(".")[0]);
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
