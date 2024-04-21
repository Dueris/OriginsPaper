package me.dueris.genesismc;

import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.TicksElapsedPower;
import me.dueris.genesismc.factory.powers.apoli.FlightHandler;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OriginScheduler {

    public static ArrayList<ApoliPower> activePowerRunners = new ArrayList<>();
    public static HashMap<Player, List<ApoliPower>> tickedPowers = new HashMap<>();
    final Plugin plugin;

    public OriginScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public void onMain(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public void offMain(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static class OriginSchedulerTree extends BukkitRunnable implements Listener {

        public static FlightHandler flightHandler = new FlightHandler();
        private final HashMap<Player, HashMap<Power, Integer>> ticksEMap = new HashMap<>();
        public OriginScheduler parent = new OriginScheduler(GenesisMC.getPlugin());

        private static void accept(Player p) {
            tickedPowers.get(p).clear();
        }

        @Override
        public String toString() {
            return "OriginSchedulerTree$run()";
        }

        @Override
        public void run() {
            for (Player p : OriginPlayerAccessor.hasPowers) {
                tickedPowers.putIfAbsent(p, new ArrayList<>());
                if (!OriginPlayerAccessor.getPowersApplied(p).contains(FlightHandler.class)) {
                    // Ensures the flight handler can still tick on players because of issues when it doesnt tick
                    flightHandler.run(p);
                }
                if (Bukkit.getServer().getCurrentTick() % 20 == 0) {
                    OriginPlayerAccessor.checkForDuplicates(p);
                }
                for (ApoliPower c : OriginPlayerAccessor.getPowersApplied(p)) {
                    if (tickedPowers.get(p).contains(c))
                        continue; // CraftPower was already ticked, we are not ticking it again.
                    tickedPowers.get(p).add(c);
                    if (c instanceof TicksElapsedPower) {
                        ((TicksElapsedPower) c).run(p, ticksEMap);
                    } else {
                        activePowerRunners.add(c);
                        c.run(p);
                    }
                }
            }

            tickedPowers.keySet().forEach(OriginSchedulerTree::accept);
        }
    }

}
