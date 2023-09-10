package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.EnumSet;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_sleep;
import static org.bukkit.Material.*;

public class PreventSleep extends CraftPower implements Listener {

    public static EnumSet<Material> beds;

    static {
        beds = EnumSet.of(WHITE_BED, LIGHT_GRAY_BED, GRAY_BED, BLACK_BED, BROWN_BED, RED_BED, ORANGE_BED, YELLOW_BED, LIME_BED, GREEN_BED,
                CYAN_BED, LIGHT_BLUE_BED, BLUE_BED, PURPLE_BED, MAGENTA_BED, PINK_BED);
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public PreventSleep() {
        this.p = p;
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
            for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                Block clickedBlock = e.getClickedBlock();
                Location blockLocation = clickedBlock.getLocation();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    boolean meetsCondition = conditionExecutor.check("block_condition", "block_conditions", e.getPlayer(), power, "origins:prevent_sleep", e.getPlayer(), null, e.getPlayer().getLocation().getBlock(), null, e.getPlayer().getItemInHand(), null);

                    if (meetsCondition) {
                        if (Boolean.parseBoolean(power.get("set_spawn_point", "false"))) {
                            player.setBedSpawnLocation(blockLocation);
                        }
                        String message = power.get("message", "origins.cant_sleep");
                        player.sendMessage(message);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_sleep";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_sleep;
    }
}
