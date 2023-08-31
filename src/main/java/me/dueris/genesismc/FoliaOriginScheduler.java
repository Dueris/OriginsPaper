package me.dueris.genesismc;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import io.papermc.paper.threadedregions.scheduler.*;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class FoliaOriginScheduler {

    final Plugin plugin;
    Player player;

    public FoliaOriginScheduler(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public static TaskScheduler getGlobalScheduler() {return GenesisMC.getGlobalScheduler();}

    private final RegionScheduler regionScheduler = Bukkit.getServer().getRegionScheduler();
    private final GlobalRegionScheduler globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();
    private final AsyncScheduler asyncScheduler = Bukkit.getServer().getAsyncScheduler();
    private final EntityScheduler entityScheduler = player.getScheduler();

    public GenesisMC.MyScheduledTask runTaskOnGlobal(BukkitRunnable runnable){
        return new GenesisMC.OriginScheduledTask(globalRegionScheduler.run(plugin, task -> runnable.run()));
    }

    public GenesisMC.MyScheduledTask runTaskTimerOnGlobal(BukkitRunnable runnable, long delay, long period){
        return new GenesisMC.OriginScheduledTask(globalRegionScheduler.runAtFixedRate(plugin, task -> runnable.run(), delay, period));
    }

    public GenesisMC.MyScheduledTask runTask(BukkitRunnable runnable) {
        runnables.add(runnable);
        return new GenesisMC.OriginScheduledTask(entityScheduler.run(plugin, task -> runnable.run(), runnable));
    }

    public Player getPlayer() {
        return player;
    }

    private long getOneIfNotPositive(long x) {
        return x <= 0 ? 1L : x;
    }

    public GenesisMC.MyScheduledTask runTaskLater(BukkitRunnable runnable, long delay) {
        runnables.add(runnable);
        delay = getOneIfNotPositive(delay);
        if (delay <= 0) {
            return runTask(runnable);
        }
        return new GenesisMC.OriginScheduledTask(entityScheduler.runDelayed(plugin, task -> runnable.run(), runnable, delay));
    }

    ArrayList<BukkitRunnable> runnables = new ArrayList<>();

    public ArrayList<BukkitRunnable> getRunnables() {
        return runnables;
    }

    public GenesisMC.MyScheduledTask runTaskTimer(BukkitRunnable runnable, long delay, long period) {
        runnables.add(runnable);
        delay = getOneIfNotPositive(delay);
        return new GenesisMC.OriginScheduledTask(entityScheduler.runAtFixedRate(plugin, task -> runnable.run(), runnable, delay, period));
    }

    public static class OriginSchedulerTree implements Runnable {
        @Override
        public void run() {
            for(Player p : OriginPlayer.hasPowers){
                if(OriginPlayer.getPowersApplied(p).isEmpty()) return;
                for(Class<? extends CraftPower> c : OriginPlayer.getPowersApplied(p)){
                            CraftPower instance = null;
                            try {
                                instance = c.newInstance();
                            } catch (InstantiationException e) {
                                throw new RuntimeException(e);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                            if (instance instanceof Listener) {
                                Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
                            }
                        p.getScheduler().execute(GenesisMC.getPlugin(), new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    c.newInstance().run(p);
                                } catch (InstantiationException e) {
                                    //rip
                                } catch (IllegalAccessException e) {
                                    //honestly skill issue
                                }
                            }
                        }, new BukkitRunnable() {
                            @Override
                            public void run() {
                                //I HATE THIS FUCKING CODE
                            }
                        }, 1);
                }
            }
        }
    }

}
