package me.dueris.genesismc;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.actions.ActionOverTime;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.factory.powers.player.damage.Burn;
import me.dueris.genesismc.factory.powers.player.damage.DamageOverTime;
import me.dueris.genesismc.factory.powers.prevent.PreventEntityRender;
import me.dueris.genesismc.factory.powers.world.FlightHandler;

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
        private final HashMap<Player, Integer> ticksEMap = new HashMap<>();

        @Override
        public String toString() {
            return "OriginSchedulerTree$run()";
        }

        @Override
        public void run() {
            for (Player p : OriginPlayerUtils.hasPowers) {
//                if (!OriginPlayer.getPowersApplied(p).contains(Gravity.class)) {
//                    Gravity gravity = new Gravity();
//                    gravity.run(p);
//                }
                if (!OriginPlayerUtils.getPowersApplied(p).contains(FlightHandler.class)) {
                    flightHandler.run(p);
                }
//                if (!OriginPlayer.getPowersApplied(p).contains(Overlay.class)) {
//                    Overlay overlay = new Overlay();
//                    overlay.run(p);
//                }
//                if (!OriginPlayer.getPowersApplied(p).contains(Invisibility.class)) {
//                    Invisibility invisibility = new Invisibility();
//                    invisibility.run(p);
//                }
//                if (OriginPlayer.getPowersApplied(p).isEmpty()) {
//                    //empty
//                }
                for (Class<? extends CraftPower> c : OriginPlayerUtils.getPowersApplied(p)) {
                    try {
                        CraftPower inst = c.newInstance();
                        if (inst instanceof Burn) {
                            ((Burn) inst).run(p, ticksEMap);
                        } else if (inst instanceof ActionOverTime) {
                            ((ActionOverTime) inst).run(p, ticksEMap);
                        } else if (inst instanceof RestrictArmor) {
                            ((RestrictArmor) inst).run(p, ticksEMap);
                        } else if (inst instanceof DamageOverTime) {
                            ((DamageOverTime) inst).run(p, ticksEMap);
                        } else if (inst instanceof PreventEntityRender) {
                            ((PreventEntityRender) inst).run(p, ticksEMap);
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
