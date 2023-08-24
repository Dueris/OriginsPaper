package me.dueris.genesismc.factory.powers.OriginsMod.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class NightVision extends CraftPower {

    @Override
    public void setActive(Boolean bool){
        if(powers_active.containsKey(getPowerFile())){
            powers_active.replace(getPowerFile(), bool);
        }else{
            powers_active.put(getPowerFile(), bool);
        }
    }

    @Override
    public Boolean getActive(){
        return powers_active.get(getPowerFile());
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            HashMap<LayerContainer, OriginContainer> origins = OriginPlayer.getOrigin(p);
            Set<LayerContainer> layers = origins.keySet();
            for (LayerContainer layer : layers) {
                if (night_vision.contains(p)) {
                    for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                        ConditionExecutor executor = new ConditionExecutor();
                        if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                            setActive(true);
                            Long strength = OriginPlayer.getOrigin(p, layer).getPowerFileFromType("origins:night_vision").getStrength();
                            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, Math.toIntExact(strength), false, false, false));
                        }else{
                            setActive(false);
                        }
                    }
                }

            }
        }
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
