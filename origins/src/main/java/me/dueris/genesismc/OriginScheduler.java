package me.dueris.genesismc;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.apoli.CreativeFlight;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class OriginScheduler {
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
		private final CreativeFlight flight = new CreativeFlight("creative_flight", "description", true, null, 0);
		public OriginScheduler parent = new OriginScheduler(GenesisMC.getPlugin());

		@Override
		public String toString() {
			return "OriginSchedulerTree$run()";
		}

		@Override
		public void run() {
			for (PowerType power : CraftApoli.getPowersFromRegistry()) {
				if (!power.hasPlayers()) continue;
				for (Player p : power.getPlayers()) {
					if (Bukkit.getServer().getCurrentTick() % 20 == 0) {
						PowerHolderComponent.checkForDuplicates(p);
					}
					try {
						power.tick(p);
						power.tickAsync(p);
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

			for (Player p : Bukkit.getOnlinePlayers()) {
				flight.tickAsync(p);
			}
		}
	}

}
