package io.github.dueris.originspaper.plugin;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.client.MinecraftClient;
import io.github.dueris.originspaper.client.resource.ResourceManager;
import io.github.dueris.originspaper.command.PehukiCommandImpl;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.content.item.OrbOfOriginsItemTracker;
import io.github.dueris.originspaper.power.type.ElytraFlightPowerType;
import io.github.dueris.originspaper.power.type.RecipePowerType;
import io.github.dueris.originspaper.registry.ModItems;
import io.github.dueris.originspaper.screen.ScreenListener;
import io.github.dueris.originspaper.util.BstatsMetrics;
import io.github.dueris.originspaper.util.GlowingEntitiesUtils;
import io.github.dueris.originspaper.util.KeybindUtil;
import io.github.dueris.originspaper.util.Scheduler;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

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
				Scheduler.tickAsyncScheduler();
			}
		}.runTaskTimerAsynchronously(this, 0L, 1L);

		PehukiCommandImpl.onLoad();
		MinecraftClient.init(OriginsPaper.context);
		RecipePowerType.registerAll();
		ModItems.registerServer();
	}

	@Override
	public void onDisable() {
		try {
			glowingEntitiesUtils.disable();
		} catch (Throwable var3) {
			OriginsPaper.LOGGER.error("An unhandled exception occurred when disabling OriginsPaper!");
		}
	}

	private void registerListeners() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new KeybindUtil(), this);
		this.getServer().getPluginManager().registerEvents(new PehukiCommandImpl(), this);
		this.getServer().getPluginManager().registerEvents(new ResourceManager(), this);
		this.getServer().getPluginManager().registerEvents(new ScreenListener(), this);
		this.getServer().getPluginManager().registerEvents(new OrbOfOriginsItemTracker(), this);
	}

	@EventHandler
	public void fixBlockGlitch(@NotNull PlayerFailMoveEvent e) {
		ServerPlayer player = ((CraftPlayer) e.getPlayer()).getHandle();
		for (ElytraFlightPowerType powerType : PowerHolderComponent.getPowerTypes(player, ElytraFlightPowerType.class, true)) {
			if (powerType.overwritingFlight) {
				e.setAllowed(true);
				e.setLogWarning(false);
			}
		}
	}
}
