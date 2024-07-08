package me.dueris.originspaper;

import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.powers.apoli.CreativeFlight;
import me.dueris.originspaper.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.originspaper.factory.powers.apoli.provider.PowerProvider;
import me.dueris.originspaper.factory.powers.originspaper.GravityPower;
import me.dueris.originspaper.factory.powers.holder.PowerType;
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
		mainThreadCalls.add(run);
	}

	public static class MainTickerThread extends BukkitRunnable implements Listener {
		private final CreativeFlight flight = new CreativeFlight("creative_flight", "description", true, null, 0);
		public OriginScheduler parent = new OriginScheduler(OriginsPaper.getPlugin());

		@Override
		public String toString() {
			return "OriginSchedulerTree$run()";
		}

		@Override
		public void run() {
			this.parent.mainThreadCalls.forEach(Runnable::run);
			this.parent.mainThreadCalls.clear();
			for (PowerType power : CraftApoli.getPowersFromRegistry()) {
				power.tick(); // Allow powers to add their own BukkitRunnables
				if (!power.hasPlayers()) continue;
				for (Player p : power.getPlayers()) {
					if (Bukkit.getServer().getCurrentTick() % 20 == 0) {
						PowerHolderComponent.checkForDuplicates(p);
					}
					try {
						power.tick(p);
					} catch (Throwable throwable) {
						String[] stacktrace = {"\n"};
						Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
						OriginsPaper.getPlugin().getLogger().severe("An unhandled exception occurred when ticking a Power! [{a}]".replace("{a}", throwable.getClass().getSimpleName()));
						String t = power.getType();
						if (t == null) t = power.key().asString();
						OriginsPaper.getPlugin().getLogger().severe(
							"Player: {a} | Power: {b} | CraftPower: {c} | Throwable: {d}"
								.replace("{a}", p.getName())
								.replace("{b}", power.getTag())
								.replace("{c}", t)
								.replace("{d}", throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage()) + stacktrace[0]
						);
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
				ConcurrentLinkedQueue<PowerType> applied = PowerHolderComponent.getPowersApplied(p);
				for (PowerType c : applied) {
					c.tickAsync(p);
				}
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				flight.tickAsync(p);
				if (!PowerHolderComponent.hasPowerType(p, GravityPower.class) && !PowerHolderComponent.hasPower(p, "origins:like_water")) {
					p.setGravity(true);
				}
			}
		}
	}

}
