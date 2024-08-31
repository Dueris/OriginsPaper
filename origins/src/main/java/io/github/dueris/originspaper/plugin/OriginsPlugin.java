package io.github.dueris.originspaper.plugin;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.content.OrbOfOrigins;
import io.github.dueris.originspaper.integration.CraftPehuki;
import io.github.dueris.originspaper.power.type.RecipePower;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.screen.ChoosingPage;
import io.github.dueris.originspaper.screen.RandomOriginPage;
import io.github.dueris.originspaper.screen.ScreenNavigator;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.ApoliScheduler;
import io.github.dueris.originspaper.util.AsyncUpgradeTracker;
import io.github.dueris.originspaper.util.BstatsMetrics;
import io.github.dueris.originspaper.util.KeybindUtil;
import io.github.dueris.originspaper.util.entity.GlowingEntitiesUtils;
import io.github.dueris.originspaper.util.entity.PlayerManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class OriginsPlugin extends JavaPlugin implements Listener {
	public static OriginsPlugin plugin;
	public static GlowingEntitiesUtils glowingEntitiesUtils;
	public static BstatsMetrics metrics;

	@Override
	public void onEnable() {
		plugin = this;
		metrics = new BstatsMetrics(this, 18536);
		OriginsPaper.server = MinecraftServer.getServer();
		glowingEntitiesUtils = new GlowingEntitiesUtils(this);
		registerListeners();

		new BukkitRunnable() {
			@Override
			public void run() {
				ApoliScheduler.tickAsyncScheduler();
			}
		}.runTaskTimerAsynchronously(this, 0L, 1L);

		CraftPehuki.onLoad();
		Bukkit.updateRecipes();
	}

	@Override
	public void onDisable() {
		try {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.closeInventory();
				player.getPersistentDataContainer()
					.set(new NamespacedKey(this, "powers"), PersistentDataType.STRING,
						PlayerPowerRepository.getOrCreateRepo(((CraftPlayer) player).getHandle()).serializePowers(new CompoundTag()).toString());
				PowerHolderComponent.unloadPowers(player);
			}

			glowingEntitiesUtils.disable();
			RecipePower.recipeMapping.clear();
			RecipePower.tags.clear();
			OriginsPaper.getRegistry().clearRegistries();
		} catch (Throwable var3) {
			OriginsPaper.LOGGER.error("An unhandled exception occurred when disabling OriginsPaper!");
		}
	}

	private void registerListeners() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new PlayerManager(), this);
		this.getServer().getPluginManager().registerEvents(new ScreenNavigator(), this);
		this.getServer().getPluginManager().registerEvents(new KeybindUtil(), this);
		this.getServer().getPluginManager().registerEvents(new AsyncUpgradeTracker(), this);
		this.getServer().getPluginManager().registerEvents(new PowerHolderComponent(), this);
		this.getServer().getPluginManager().registerEvents(new CraftPehuki(), this);
		OriginsPaper.getRegistry().retrieve(Registries.POWER).values().forEach(powerType -> {
			this.getServer().getPluginManager().registerEvents(powerType, this);
		});
	}

	@EventHandler
	public void loadEvent(ServerLoadEvent e) {
		ChoosingPage.registerInstances();
		ScreenNavigator.layerPages.values().forEach(pages -> pages.add(pages.size(), new RandomOriginPage()));
		OrbOfOrigins.init();
	}

}
