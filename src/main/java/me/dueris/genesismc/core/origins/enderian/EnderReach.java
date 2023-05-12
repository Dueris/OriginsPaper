package me.dueris.genesismc.core.origins.enderian;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.Nullable;

public class EnderReach implements Listener {

    @EventHandler
    public void OnClickREACH(PlayerArmSwingEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {
            Player p = e.getPlayer();

            int range = 9;

            RayTraceResult result = p.rayTraceEntities(range, false);

            if (result != null && result.getHitEntity() instanceof LivingEntity target) {

                double maxRange = p.getGameMode() == GameMode.CREATIVE ? 4.5 : 3.0;

                boolean canAttack = result.getHitPosition().distanceSquared(p.getEyeLocation().toVector()) > (maxRange * maxRange);

                if (canAttack) {
                    p.attack(target);
                }

            }
        }
    }
}


