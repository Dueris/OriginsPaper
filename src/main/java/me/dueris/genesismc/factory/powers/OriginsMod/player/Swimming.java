package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Swimming extends CraftPower {

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
        for(Player p : Bukkit.getOnlinePlayers()){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                if(swimming.contains(p)){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(!conditionExecutor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                        setActive(false);
                        return;
                    }else{
                        p.setSwimming(true);
                        setActive(true);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:swimming";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return swimming;
    }
}
