package me.dueris.genesismc;

import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.TicksElapsedPower;
import me.dueris.genesismc.factory.powers.apoli.FlightHandler;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class OriginScheduler {

    public static ArrayList<Class<? extends CraftPower>> activePowerRunners = new ArrayList<>();
    final Plugin plugin;
    ArrayList<BukkitRunnable> runnables = new ArrayList<>();

    public OriginScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public BukkitTask runTask(BukkitRunnable runnable) {
        runnables.add(runnable);
        return runnable.runTask(plugin);
    }

    private long getOneIfNotPositive(long x) {
        return x <= 0 ? 1L : x;
    }

    public BukkitTask runTaskLater(BukkitRunnable runnable, long delay) {
        runnables.add(runnable);
        delay = getOneIfNotPositive(delay);
        if (delay <= 0) {
            return runTask(runnable);
        }
        return runnable.runTaskLater(plugin, delay);
    }

    public ArrayList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    public BukkitTask runTaskTimer(BukkitRunnable runnable, long delay, long period) {
        runnables.add(runnable);
        delay = getOneIfNotPositive(delay);
        return runnable.runTaskTimer(GenesisMC.getPlugin(), delay, period);
    }

    public static class OriginSchedulerTree extends BukkitRunnable implements Listener {

        public static FlightHandler flightHandler = new FlightHandler();
        private final HashMap<Player, HashMap<PowerContainer, Integer>> ticksEMap = new HashMap<>();

        @Override
        public String toString() {
            return "OriginSchedulerTree$run()";
        }

        @Override
        public void run() {
            for (Player p : OriginPlayerAccessor.hasPowers) {
                if (!OriginPlayerAccessor.getPowersApplied(p).contains(FlightHandler.class)) {
                    // Ensures the flight handler can still tick on players because of issues when it doesnt tick
                    flightHandler.run(p);
                }
                for (Class<? extends CraftPower> c : OriginPlayerAccessor.getPowersApplied(p)) {
                    try {
                        CraftPower inst = c.newInstance();
                        if (inst instanceof TicksElapsedPower) {
                            ((TicksElapsedPower) inst).run(p, ticksEMap);
                        } else {
                            activePowerRunners.add(c);
                            inst.run(p);
                        }

                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
