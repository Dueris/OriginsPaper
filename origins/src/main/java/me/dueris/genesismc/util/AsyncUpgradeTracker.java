package me.dueris.genesismc.util;

import me.dueris.calio.util.holders.TriPair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncUpgradeTracker implements Listener {
	public static ConcurrentHashMap<Origin, TriPair<String, NamespacedKey, String>/*String advancement, NamespacedKey identifier, String announcement*/> upgrades = new ConcurrentHashMap<>();
	public static String NO_ANNOUNCEMENT = "no_announcement_found";

	@EventHandler
	public void startEvent(ServerLoadEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				MinecraftServer server = GenesisMC.server;
				for (Map.Entry<Origin, TriPair<String, NamespacedKey, String>> entry : upgrades.entrySet()) {
					for (CraftPlayer player : ((CraftServer) Bukkit.getServer()).getOnlinePlayers()) {
						for (Layer layer : CraftApoli.getLayersFromRegistry()) {
							if (PowerHolderComponent.getOrigin(player, layer).equals(entry.getKey())) {
								String advancement = entry.getValue().a();
								NamespacedKey originToSet = entry.getValue().b();
								String announcement = entry.getValue().c();

								AdvancementHolder advancementHolder = server.getAdvancements().get(CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(advancement)));
								if (advancementHolder == null) {
									GenesisMC.getPlugin().getLogger().severe("Advancement \"{}\" did not exist but was referenced in the an origin upgrade!".replace("{}", advancement));
								}

								AdvancementProgress progress = player.getHandle().getAdvancements().getOrStartProgress(advancementHolder);
								if (progress.isDone()) {
									PowerHolderComponent.setOrigin(player, layer, GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN).get(originToSet));
									if (!announcement.equals(NO_ANNOUNCEMENT)) {
										player.sendMessage(announcement);
									}
								}
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(GenesisMC.getPlugin(), 0, 5);
	}
}
