package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;

public class DisableRegen extends CraftPower implements Listener {

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

    @EventHandler
    public void disable(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (disable_regen.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    ConditionExecutor executor = new ConditionExecutor();
                    if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                        setActive(true);
                        if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
                            e.setAmount(0);
                            e.setCancelled(true);
                        }
                    }else{
                        setActive(false);
                    }
                }
            }
        }
    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:disable_regen";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return disable_regen;
    }
}
