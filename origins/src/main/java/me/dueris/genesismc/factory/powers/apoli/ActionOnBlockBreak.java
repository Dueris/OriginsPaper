package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.BlockPos;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnBlockBreak extends CraftPower implements Listener {
    public static HashMap<Player, Boolean> playersMining = new HashMap<>();
    public static HashMap<Player, BlockPos> playersMiningBlockPos = new HashMap<>();

    @EventHandler
    public void brek(BlockBreakEvent e) {
        Player actor = e.getPlayer();

        if (!getPlayersWithPower().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power powerContainer : OriginPlayerAccessor.getPowers(actor, getType(), layer)) {
                if (powerContainer == null) continue;
                if (!(ConditionExecutor.testBlock(powerContainer.getJsonObject("block_condition"), (CraftBlock) e.getBlock()) && ConditionExecutor.testEntity(powerContainer.getJsonObject("condition"), (CraftEntity) e.getPlayer())))
                    return;
                boolean pass = true;
                if (powerContainer.getBooleanOrDefault("only_when_harvested", true)) {
                    pass = ((CraftPlayer) actor).getHandle().hasCorrectToolForDrops(((CraftBlock) e.getBlock()).getNMS());
                }

                setActive(actor, powerContainer.getTag(), true);
                if (pass) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Actions.executeBlock(e.getBlock().getLocation(), powerContainer.getJsonObject("block_action"));
                            Actions.executeEntity(e.getPlayer(), powerContainer.getJsonObject("entity_action"));
                        }
                    }.runTaskLater(GenesisMC.getPlugin(), 1);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setActive(actor, powerContainer.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }
    // Checks for if the player is mining

    @EventHandler
    public void breakTick(BlockDamageEvent e) {
        if (!e.isCancelled()) {
            playersMining.put(e.getPlayer(), true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    playersMining.put(e.getPlayer(), false);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);
            playersMiningBlockPos.put(e.getPlayer(), new BlockPos(e.getBlock().getLocation().getBlockX(), e.getBlock().getLocation().getBlockY(), e.getBlock().getLocation().getBlockZ()));
        }
    }
    // End

    @Override
    public String getType() {
        return "apoli:action_on_block_break";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return action_on_block_break;
    }

}
