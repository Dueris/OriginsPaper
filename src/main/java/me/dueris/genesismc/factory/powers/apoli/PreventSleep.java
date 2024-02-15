package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_sleep;
import static org.bukkit.Material.*;

public class PreventSleep extends CraftPower implements Listener {

    public static EnumSet<Material> beds;

    static {
        beds = EnumSet.of(WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED, LIME_BED, GREEN_BED,
                CYAN_BED, LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED);
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runD(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction().isLeftClick()) return;
        if (beds.contains(e.getClickedBlock().getType())) {
            Player player = e.getPlayer();
            for (LayerContainer layer : CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                Block clickedBlock = e.getClickedBlock();
                Location blockLocation = clickedBlock.getLocation();
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    boolean meetsCondition = conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), power, "apoli:prevent_sleep", e.getPlayer(), null, e.getPlayer().getLocation().getBlock(), null, e.getPlayer().getItemInHand(), null);

                    if (meetsCondition) {
                        if (power.getBooleanOrDefault("set_spawn_point", false)) {
                            player.setBedSpawnLocation(blockLocation);
                        }
                        String message = power.getStringOrDefault("message", LangConfig.getLocalizedString(player, "origins.cant_sleep"));
                        player.sendMessage(message);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_sleep";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_sleep;
    }
}
