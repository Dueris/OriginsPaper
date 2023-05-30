package me.dueris.genesismc.core.factory.powers.custom;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.targetActionOnHit;

public class TargetActionOnHit extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (targetActionOnHit.containsKey(OriginPlayer.getOriginTag(p))) {
                String originTag = OriginPlayer.getOriginTag(p);
                String powerTag = targetActionOnHit.get(originTag);

                String powerClass = CraftApoli.getPowerClass(originTag, powerTag);
                String powerAttribute = CraftApoli.getPowerAttribute(originTag, powerTag);
                Float powerMultiplier = CraftApoli.getPowerMultiplier(originTag, powerTag);
            }
        }
    }
}
