package me.dueris.genesismc.core.origins.enderian;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class EnderReach implements Listener {

    @EventHandler
    public void OnClickREACH(PlayerArmSwingEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-enderian")) {

            Player p = e.getPlayer();
            Entity entity = p.getTargetEntity(9, false);
            if(entity instanceof LivingEntity target){
               p.attack(target);
            }
            Block blockTarget = p.getTargetBlockExact(6);
//            if (blockTarget != null) p.breakBlock(blockTarget);
        }

    }
}


