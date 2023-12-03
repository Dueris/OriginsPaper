package me.dueris.genesismc;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.FlightHandler;
import me.dueris.genesismc.factory.powers.Overlay;
import me.dueris.genesismc.factory.powers.actions.ActionOverTime;
import me.dueris.genesismc.factory.powers.player.Gravity;
import me.dueris.genesismc.factory.powers.player.Invisibility;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.factory.powers.player.damage.Burn;
import me.dueris.genesismc.factory.powers.player.damage.DamageOverTime;
import me.dueris.genesismc.factory.powers.prevent.PreventEntityRender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class FoliaOriginScheduler {

    final Plugin plugin;

    public FoliaOriginScheduler(Plugin plugin) {
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

    ArrayList<BukkitRunnable> runnables = new ArrayList<>();

    public ArrayList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    public BukkitTask runTaskTimer(BukkitRunnable runnable, long delay, long period) {
        runnables.add(runnable);
        delay = getOneIfNotPositive(delay);
        return runnable.runTaskTimer(GenesisMC.getPlugin(), delay, period);
    }

    public static class OriginSchedulerTree extends BukkitRunnable implements Listener {

        @Override
        public String toString() {
            return "OriginSchedulerTree$run()";
        }

        public static ArrayList<Player> mimic_warden = new ArrayList<>();

        private final HashMap<Player, Integer> ticksEMap = new HashMap<>();

        @Override
        public void run() {
            for (Player p : OriginPlayer.hasPowers) {
//                if (!OriginPlayer.getPowersApplied(p).contains(Gravity.class)) {
//                    Gravity gravity = new Gravity();
//                    gravity.run(p);
//                }
//                if (!OriginPlayer.getPowersApplied(p).contains(FlightHandler.class)) {
//                    FlightHandler flightHandler = new FlightHandler();
//                    flightHandler.run(p);
//                }
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
                for (Class<? extends CraftPower> c : OriginPlayer.getPowersApplied(p)) {
                    try {
                        if (c.newInstance() instanceof Burn) {
                            ((Burn) c.newInstance()).run(p, ticksEMap);
                        } else if (c.newInstance() instanceof ActionOverTime) {
                            ((ActionOverTime) c.newInstance()).run(p, ticksEMap);
                        } else if (c.newInstance() instanceof RestrictArmor) {
                            ((RestrictArmor) c.newInstance()).run(p, ticksEMap);
                        } else if (c.newInstance() instanceof DamageOverTime) {
                            ((DamageOverTime) c.newInstance()).run(p, ticksEMap);
                        } else if(c.newInstance() instanceof PreventEntityRender) {
                            ((PreventEntityRender) c.newInstance()).run(p, ticksEMap);
                        } else {
                            c.newInstance().run(p);
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
