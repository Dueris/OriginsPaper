package me.dueris.genesismc.core.factory.powers.OriginsMod;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.Phasing;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.overlay;

public class Overlay extends BukkitRunnable {
    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()){
            if(overlay.contains(player)){
                for(OriginContainer origin : OriginPlayer.getOrigin(player).values()){
                    if(ConditionExecutor.check("condition", player, origin, "origins:overlay", null, player)){
                        Phasing.initializePhantomOverlay(player);
                    }else{
                        Phasing.deactivatePhantomOverlay(player);
                    }
                }
            }else{
                Phasing.deactivatePhantomOverlay(player);
            }
        }
    }
}
