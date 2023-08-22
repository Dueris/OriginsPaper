package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.prevent.PreventSuperClass.prevent_sprinting;

public class PreventSprinting extends BukkitRunnable {

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(prevent_sprinting.contains(p)){
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if(conditionExecutor.check("condition", "conditions", p, origin, "origins:prevent_sprinting", null, p)){
                        p.setSprinting(false);
                    }
                }
            }
        }
    }
}
