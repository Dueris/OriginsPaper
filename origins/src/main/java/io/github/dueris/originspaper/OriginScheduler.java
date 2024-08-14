package io.github.dueris.originspaper;

import io.github.dueris.originspaper.power.CreativeFlightPower;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.power.provider.OriginSimpleContainer;
import io.github.dueris.originspaper.power.provider.PowerProvider;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OriginScheduler {
	final Plugin plugin;
	private final ConcurrentLinkedQueue<Runnable> mainThreadCalls = new ConcurrentLinkedQueue<>();

	public OriginScheduler(Plugin plugin) {
		this.plugin = plugin;
	}

	public void scheduleMainThreadCall(Runnable run) {
		this.mainThreadCalls.add(run);
	}

	public static class MainTickerThread extends BukkitRunnable implements Listener {
		public OriginScheduler parent = new OriginScheduler(OriginsPaper.getPlugin());

		public String toString() {
			return "OriginSchedulerTree$run()";
		}

		public void run() {
			this.parent.mainThreadCalls.forEach(Runnable::run);
			this.parent.mainThreadCalls.clear();

			for (PowerType power : OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).values()) {
				power.tick();
				if (power.hasPlayers()) {
					for (Player p : power.getPlayers()) {
						if (Bukkit.getServer().getCurrentTick() % 20 == 0) {
							PowerHolderComponent.checkForDuplicates((CraftPlayer) p.getBukkitEntity());
						}

						try {
							power.tick(p);
						} catch (Throwable var8) {
							String[] stacktrace = new String[]{"\n"};
							Arrays.stream(var8.getStackTrace())
								.map(StackTraceElement::toString)
								.forEach(string -> stacktrace[0] = stacktrace[0] + "\tat " + string + "\n");
							OriginsPaper.getPlugin()
								.getLog4JLogger()
								.error("An unhandled exception occurred when ticking a Power! [{}]", var8.getClass().getSimpleName());
							String t = power.getType();
							if (t == null) {
								t = power.key().toString();
							}

							OriginsPaper.getPlugin()
								.getLog4JLogger()
								.error("Player: {} | Power: {} | CraftPower: {} | Throwable: {} {}", p.getName(), power.getTag(), t, var8.getMessage() == null ? var8.getClass().getSimpleName() : var8.getMessage(), stacktrace[0]);
						}
					}
				}
			}

			for (PowerProvider provider : OriginSimpleContainer.registeredPowers) {
				provider.tick();

				for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
					provider.tick(p);
				}
			}
		}

		public void tickAsyncScheduler() {
			for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
				for (PowerType c : PowerHolderComponent.getPowersApplied(p)) {
					c.tickAsync(((CraftPlayer) p).getHandle());
				}
			}

			for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
				CreativeFlightPower.tickPlayer(((CraftPlayer) p).getHandle(), null);
//				if (!PowerHolderComponent.hasPowerType(p, GravityPower.class) && !PowerHolderComponent.hasPower(p, "origins:like_water")) {
//					p.setGravity(true);
//				}
			}
		}
	}
}
