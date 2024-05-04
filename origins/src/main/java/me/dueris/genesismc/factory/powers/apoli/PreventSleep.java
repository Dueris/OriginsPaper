package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.EnumSet;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_sleep;
import static org.bukkit.Material.*;

public class PreventSleep extends CraftPower implements Listener {
    public static EnumSet<Material> beds;

    static {
        beds = EnumSet.of(WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED, LIME_BED, GREEN_BED,
                CYAN_BED, LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED);
    }

    @EventHandler
    public void runD(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction().isLeftClick()) return;
        if (beds.contains(e.getClickedBlock().getType())) {
            Player player = e.getPlayer();
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                Block clickedBlock = e.getClickedBlock();
                Location blockLocation = clickedBlock.getLocation();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getType(), layer)) {
                    boolean meetsCondition = ConditionExecutor.testBlock(power.getJsonObject("block_condition"), (CraftBlock) player.getLocation().getBlock());

                    if (meetsCondition) {
                        if (power.getBooleanOrDefault("set_spawn_point", false)) {
                            player.setBedSpawnLocation(blockLocation);
                        }
                        String message = power.getStringOrDefault("message", "text.apoli.cannot_sleep");
                        // Origins Mod translation
                        if (message.equalsIgnoreCase("text.apoli.cannot_sleep")) message = "You cannot sleep";
                        if (message.equalsIgnoreCase("origins.avian_sleep_fail"))
                            message = "You need fresh air to sleep";

                        player.sendMessage(message);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:prevent_sleep";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return prevent_sleep;
    }
}
