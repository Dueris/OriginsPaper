package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.fresh_air;
import static org.bukkit.Material.*;

public class FreshAir implements Listener {

    public static EnumSet<Material> beds;
    static {
        beds = EnumSet.of(WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED, LIME_BED, GREEN_BED,
                CYAN_BED,LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (fresh_air.contains(origintag)) {
            if (e.getPlayer().getWorld().getEnvironment() == World.Environment.NORMAL) {
                if (e.getBed().getY() <= 99) {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar("You need fresh air to sleep");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (fresh_air.contains(origintag)) {
            Player p = e.getPlayer();
            if (p.getWorld().getEnvironment() == World.Environment.NORMAL) {
                Block block = e.getClickedBlock();
                for (Material bed : beds) {
                    if (e.getClickedBlock() != null) {  //added null check in dev
                        if (block.getType() == bed) {
                            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                if (block.getY() <= 99) {
                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
