package io.github.dueris.originspaper.plugin;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.client.MinecraftClient;
import io.github.dueris.originspaper.client.resource.ResourceManager;
import io.github.dueris.originspaper.command.PehukiCommandImpl;
import io.github.dueris.originspaper.power.type.RecipePowerType;
import io.github.dueris.originspaper.util.BstatsMetrics;
import io.github.dueris.originspaper.util.GlowingEntitiesUtils;
import io.github.dueris.originspaper.util.KeybindUtil;
import io.github.dueris.originspaper.util.Scheduler;
import net.minecraft.server.MinecraftServer;
import org.bukkit.event.Listener;
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
				Scheduler.tickAsyncScheduler();
			}
		}.runTaskTimerAsynchronously(this, 0L, 1L);

		PehukiCommandImpl.onLoad();
		MinecraftClient.init(OriginsPaper.bootContext);
		RecipePowerType.registerAll();
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
	}

}
