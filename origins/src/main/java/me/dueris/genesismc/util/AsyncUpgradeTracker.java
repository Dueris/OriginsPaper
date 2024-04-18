package me.dueris.genesismc.util;

import me.dueris.calio.util.holders.TriPair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;

public class AsyncUpgradeTracker implements Listener {
    private static final List<Runnable> ticks = new ArrayList<>();
    public static HashMap<Origin, TriPair/*String advancement, NamespacedKey identifier, String announcement*/> upgrades = new HashMap<>();
    public static AsyncUpgradeTracker tracker;
    public static String NO_ANNOUNCEMENT = "no_announcement_found";

    public static AsyncUpgradeTracker startTicking() {
        upgrades.keySet().stream().map(Origin::getTag).forEach(out::println);
        ExecutorService scheduler = Executors.newFixedThreadPool(1, new NamedTickThreadFactory("OriginAsyncUpgradeTracker"));
        long delay = 2; // 2 milliseconds

        Timer timer = new Timer("UpgradeTimer");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scheduler.submit(() -> ticks.forEach(Runnable::run));
            }
        }, 0, delay);

        return new AsyncUpgradeTracker();
    }

    public void scheduleTick() {
        ticks.add(() -> {
            MinecraftServer server = GenesisMC.server;
            for (Map.Entry<Origin, TriPair> entry : upgrades.entrySet()) {
                for (CraftPlayer player : ((CraftServer) Bukkit.getServer()).getOnlinePlayers()) {
                    OriginPlayerAccessor.getOrigin(player).keySet().forEach(layer -> {
                        if (OriginPlayerAccessor.getOrigin(player, layer).equals(entry.getKey())) {
                            String advancement = (String) entry.getValue().first;
                            NamespacedKey originToSet = (NamespacedKey) entry.getValue().second;
                            String announcement = (String) entry.getValue().third;

                            AdvancementHolder advancementHolder = server.getAdvancements().get(CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(advancement)));
                            if (advancementHolder == null) {
                                GenesisMC.getPlugin().getLogger().severe("Advancement \"{}\" did not exist but was referenced in the an origin upgrade!".replace("{}", advancement));
                            }

                            AdvancementProgress progress = player.getHandle().getAdvancements().getOrStartProgress(advancementHolder);
                            if (progress.isDone()) {
                                OriginPlayerAccessor.setOrigin(player, layer, (Origin) GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN).get(originToSet));
                                if (!announcement.equals(NO_ANNOUNCEMENT)) {
                                    player.sendMessage(announcement);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @EventHandler
    public void startEvent(ServerLoadEvent e) {
        tracker = AsyncUpgradeTracker.startTicking();
        new BukkitRunnable() {
            @Override
            public void run() {
                tracker.scheduleTick();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 2);
    }
}
