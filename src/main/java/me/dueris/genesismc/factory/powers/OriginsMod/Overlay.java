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
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(overlay.contains(player)){
                for(OriginContainer origin : OriginPlayer.getOrigin(player).values()){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("condition", "conditions", player, origin, "origins:overlay", null, player)){
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                        Phasing.initializePhantomOverlay(player);
                    }else{
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
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
