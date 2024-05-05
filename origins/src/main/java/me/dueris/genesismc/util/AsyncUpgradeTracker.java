package me.dueris.genesismc.util;

import me.dueris.calio.util.holders.TriPair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncUpgradeTracker implements Listener {
	private static final ConcurrentLinkedQueue<Runnable> ticks = new ConcurrentLinkedQueue<>();
	public static ConcurrentHashMap<Origin, TriPair/*String advancement, NamespacedKey identifier, String announcement*/> upgrades = new ConcurrentHashMap<>();
	public static AsyncUpgradeTracker tracker;
	public static String NO_ANNOUNCEMENT = "no_announcement_found";

	public static AsyncUpgradeTracker startTicking() {
		if (!upgrades.keySet().isEmpty())
			GenesisMC.getPlugin().getLogger().info("Loaded {} upgradable origins into AsyncUpgradeTracker!".replace("{}", String.valueOf(upgrades.keySet().size())));
		return new AsyncUpgradeTracker();
	}

	public void scheduleTick() {
		ticks.add(() -> {
			MinecraftServer server = GenesisMC.server;
			for (Map.Entry<Origin, TriPair> entry : upgrades.entrySet()) {
				for (CraftPlayer player : ((CraftServer) Bukkit.getServer()).getOnlinePlayers()) {
					for (Layer layer : CraftApoli.getLayersFromRegistry()) {
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
					}
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
				ticks.forEach(Runnable::run);
			}
		}.runTaskTimerAsynchronously(GenesisMC.getPlugin(), 0, 5);
	}
}
