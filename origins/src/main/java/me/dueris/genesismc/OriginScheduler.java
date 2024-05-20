package me.dueris.genesismc;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.apoli.CreativeFlight;
import me.dueris.genesismc.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.factory.powers.genesismc.GravityPower;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OriginScheduler {
	final Plugin plugin;
	private final ConcurrentLinkedQueue<Runnable> tasksOnMain = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<Runnable> tasksOffMain = new ConcurrentLinkedQueue<>();

	public OriginScheduler(Plugin plugin) {
		this.plugin = plugin;
	}

	public void onMain(Runnable runnable) {
		this.tasksOnMain.add(runnable);
	}

	public void offMain(Runnable runnable) {
		this.tasksOffMain.add(runnable);
	}

	public static class MainTickerThread extends BukkitRunnable implements Listener {
		private final CreativeFlight flight = new CreativeFlight("creative_flight", "description", true, null, 0);
		public OriginScheduler parent = new OriginScheduler(GenesisMC.getPlugin());

		@Override
		public String toString() {
			return "OriginSchedulerTree$run()";
		}

		@Override
		public void run() {
			ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>(parent.tasksOnMain);
			parent.tasksOnMain.clear();
			tasks.forEach(Runnable::run);
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
						GenesisMC.getPlugin().getLogger().severe("An unhandled exception occurred when ticking a Power! [{a}]".replace("{a}", throwable.getClass().getSimpleName()));
						String t = power.getType();
						if (t == null) t = power.getKey().asString();
						GenesisMC.getPlugin().getLogger().severe(
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
			ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>(parent.tasksOffMain);
			parent.tasksOffMain.clear();
			tasks.forEach(Runnable::run);
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
