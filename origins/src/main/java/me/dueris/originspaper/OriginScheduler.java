package me.dueris.originspaper;

import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.factory.powers.provider.OriginSimpleContainer;
import me.dueris.originspaper.factory.powers.provider.PowerProvider;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
		// todo
//		private final CreativeFlight flight = new CreativeFlight("creative_flight", "description", true, null, 0);
		public OriginScheduler parent = new OriginScheduler(OriginsPaper.getPlugin());

		public String toString() {
			return "OriginSchedulerTree$run()";
		}

		public void run() {
			this.parent.mainThreadCalls.forEach(Runnable::run);
			this.parent.mainThreadCalls.clear();

			for (PowerType power : CraftApoli.getPowersFromRegistry()) {
				power.tick();
				if (power.hasPlayers()) {
					for (Player p : power.getPlayers()) {
						if (Bukkit.getServer().getCurrentTick() % 20 == 0) {
							PowerHolderComponent.checkForDuplicates(p);
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

				for (Player p : Bukkit.getOnlinePlayers()) {
					provider.tick(p);
				}
			}
		}

		public void tickAsyncScheduler() {
			for (Player p : PowerHolderComponent.hasPowers) {
				for (PowerType c : PowerHolderComponent.getPowersApplied(p)) {
					c.tickAsync(p);
				}
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				//this.flight.tickAsync(p);
//				if (!PowerHolderComponent.hasPowerType(p, GravityPower.class) && !PowerHolderComponent.hasPower(p, "origins:like_water")) {
//					p.setGravity(true);
//				}
			}
		}
	}
}
