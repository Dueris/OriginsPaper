package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_falling;

public class ModifyFallingPower implements Listener {
    @EventHandler
    public void run(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(modify_falling.contains(p)){
            if(p.getVelocity().getY() < 0){
                @NotNull Vector velocity = p.getVelocity();
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    velocity.setY(Integer.parseInt(origin.getPowerFileFromType("origins:modify_falling").get("velocity", null).toString()));
                }
            }
        }
    }

    @EventHandler
    public void run(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(modify_falling.contains(p)){
                for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                    if(Boolean.getBoolean(origin.getPowerFileFromType("origins:modify_falling").get("take_fall_damage", "true").toString())){
                        if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
                            e.setDamage(0);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }

    }
}
