package me.dueris.genesismc;

import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.apoli.CreativeFlight;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OriginScheduler {
	public static ConcurrentHashMap<Player, List<ApoliPower>> tickedPowers = new ConcurrentHashMap<>();
	final Plugin plugin;

	public OriginScheduler(Plugin plugin) {
		this.plugin = plugin;
	}

	public void onMain(Runnable runnable) {
		plugin.getServer().getScheduler().runTask(plugin, runnable);
	}

	public void offMain(Runnable runnable) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
	}

	public static class OriginSchedulerTree extends BukkitRunnable implements Listener {

		public static CreativeFlight flightHandler = new CreativeFlight();
		private final HashMap<Player, HashMap<Power, Integer>> ticksEMap = new HashMap<>();
		public OriginScheduler parent = new OriginScheduler(GenesisMC.getPlugin());

		private static void accept(Player p) {
			tickedPowers.get(p).clear();
		}

		@Override
		public String toString() {
			return "OriginSchedulerTree$run()";
		}

		@Override
		public void run() {
			for (Player p : OriginPlayerAccessor.hasPowers) {
				if (Bukkit.getServer().getCurrentTick() % 20 == 0) {
					OriginPlayerAccessor.checkForDuplicates(p);
				}
				ConcurrentLinkedQueue<ApoliPower> applied = OriginPlayerAccessor.getPowersApplied(p);
				for (ApoliPower c : applied) {
					if (!c.getPlayersWithPower().contains(p)) {
						c.doesntHavePower(p); // Allow powers to tick on players that don't have that power
						continue; // Player doesn't have this power or the power isn't assigned
					}
					if (tickedPowers.get(p).contains(c))
						continue; // CraftPower was already ticked, we are not ticking it again.
					tickedPowers.get(p).add(c);

					for (Power power : OriginPlayerAccessor.getPowers(p, c.getType(), c)) {
						try {
							c.run(p, power);
						} catch (Throwable throwable) {
							String[] stacktrace = {"\n"};
							Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
							GenesisMC.getPlugin().getLogger().severe("An unhandled exception occurred when ticking a Power! [{a}]".replace("{a}", throwable.getClass().getSimpleName()));
							String t = c.getType();
							if (t == null) t = c.getKey().asString();
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
			}

			tickedPowers.keySet().forEach(OriginSchedulerTree::accept);
		}

		public void tickAsyncScheduler() {
			for (Player p : OriginPlayerAccessor.hasPowers) {
				ConcurrentLinkedQueue<ApoliPower> applied = OriginPlayerAccessor.getPowersApplied(p);
				if (!applied.stream().map(Object::getClass).toList().contains(CreativeFlight.class)) {
					flightHandler.runAsync(p, null);
				}
				for (ApoliPower c : applied) {
					for (Power power : OriginPlayerAccessor.getPowers(p, c.getType())) {
						c.runAsync(p, power);
					}
				}
			}
		}
	}

}
