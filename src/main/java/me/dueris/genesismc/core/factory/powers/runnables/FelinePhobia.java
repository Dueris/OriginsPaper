package me.dueris.genesismc.core.factory.powers.runnables;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.felinephobia;

public class FelinePhobia extends BukkitRunnable {
    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            if(felinephobia.contains(OriginPlayer.getOriginTag(p))){
            List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
            for (Entity tmp : nearby2)
                if (tmp instanceof Cat)
                    ((Damageable) p).damage(1);
        }
      }

    }
}
