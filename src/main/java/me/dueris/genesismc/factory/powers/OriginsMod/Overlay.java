package me.dueris.genesismc.factory.powers.OriginsMod;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.OriginsMod.player.Phasing;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Overlay extends CraftPower {

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
        for(Player player : Bukkit.getOnlinePlayers()){
            if(overlay.contains(player)){
                for(OriginContainer origin : OriginPlayer.getOrigin(player).values()){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("condition", "conditions", player, origin, "origins:overlay", null, player)){
                        setActive(true);
                        Phasing.initializePhantomOverlay(player);
                    }else{
                        setActive(false);
                        Phasing.deactivatePhantomOverlay(player);
                    }
                }
            }else{
                Phasing.deactivatePhantomOverlay(player);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:overlay";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return overlay;
    }
}
