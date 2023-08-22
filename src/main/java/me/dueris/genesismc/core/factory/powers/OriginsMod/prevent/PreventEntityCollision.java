package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_entity_collision;

public class PreventEntityCollision extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                if(prevent_entity_collision.contains(p)){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("bientity_condition", "bientity_condition", p, origin, "origins:prevent_entity_collision", null, p)){
                        p.setCollidable(false);
                    }else{
                        p.setCollidable(true);
                    }
                }else{
                    p.setCollidable(true);
                }
            }
        }
    }
}
