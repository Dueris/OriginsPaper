package io.github.dueris.originspaper.util;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.type.CreativeFlightPower;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.screen.GuiTicker;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class ApoliScheduler {
	public static ApoliScheduler INSTANCE = new ApoliScheduler();
	private final Int2ObjectMap<List<Consumer<MinecraftServer>>> taskQueue = new Int2ObjectOpenHashMap<>();
	private int currentTick = 0;

	public static void tickAsyncScheduler() {
		for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
			for (PowerType c : PowerHolderComponent.getPowersApplied(p)) {
				c.tickAsync(((CraftPlayer) p).getHandle());
			}
		}

		for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
			CreativeFlightPower.tickPlayer(((CraftPlayer) p).getHandle(), null);
		}

		GuiTicker.tick();
	}

	public void tick(@NotNull MinecraftServer m) {
		this.currentTick = m.getTickCount();
		List<Consumer<MinecraftServer>> runnables = this.taskQueue.remove(this.currentTick);
		if (runnables != null) for (int i = 0; i < runnables.size(); i++) {
			Consumer<MinecraftServer> runnable = runnables.get(i);
			runnable.accept(m);

			if (runnable instanceof Repeating repeating) {// reschedule repeating tasks
				if (repeating.shouldQueue(this.currentTick))
					this.queue(runnable, ((Repeating) runnable).next);
			}
		}

		tick();
	}

	/**
	 * queue a one time task to be executed on the server thread
	 *
	 * @param tick how many ticks in the future this should be called, where 0 means at the end of the current tick
	 * @param task the action to perform
	 */
	public void queue(Consumer<MinecraftServer> task, int tick) {
		this.taskQueue.computeIfAbsent(this.currentTick + tick + 1, t -> new LinkedList<>()).add(task);
	}

	/**
	 * schedule a repeating task that is executed infinitely every n ticks
	 *
	 * @param task     the action to perform
	 * @param tick     how many ticks in the future this event should first be called
	 * @param interval the number of ticks in between each execution
	 */
	public void repeating(Consumer<MinecraftServer> task, int tick, int interval) {
		this.repeatWhile(task, null, tick, interval);
	}

	/**
	 * repeat the given task until the predicate returns false
	 *
	 * @param task     the action to perform
	 * @param requeue  whether or not to reschedule the task again, with the parameter being the current tick
	 * @param tick     how many ticks in the future this event should first be called
	 * @param interval the number of ticks in between each execution
	 */
	public void repeatWhile(Consumer<MinecraftServer> task, IntPredicate requeue, int tick, int interval) {
		this.queue(new Repeating(task, requeue, interval), tick);
	}

	/**
	 * repeat the given task n times more than 1 time
	 *
	 * @param task  the action to perform
	 * @param times the number of <b>additional</b> times the task should be scheduled
	 * @param tick  how many ticks in the future this event should first be called
	 *              * @param interval the number of ticks in between each execution
	 */
	public void repeatN(Consumer<MinecraftServer> task, int times, int tick, int interval) {
		this.repeatWhile(task, new IntPredicate() {
			private int remaining = times;

			@Override
			public boolean test(int value) {
				return this.remaining-- > 0;
			}
		}, tick, interval);
	}

	public String toString() {
		return "OriginSchedulerTree$run()";
	}

	public void tick() {
		for (PowerType power : OriginsPaper.getRegistry().retrieve(Registries.POWER).values()) {
			power.tick();
			if (power.hasPlayers()) {
				for (Player p : power.getPlayers()) {
					if (Bukkit.getServer().getCurrentTick() % 20 == 0) {
						PowerHolderComponent.checkForDuplicates((CraftPlayer) p.getBukkitEntity());
					}

					try {
						if (power.shouldTick()) {
							power.tick(p);
						}
					} catch (Throwable var8) {
						String[] stacktrace = new String[]{"\n"};
						Arrays.stream(var8.getStackTrace())
							.map(StackTraceElement::toString)
							.forEach(string -> stacktrace[0] = stacktrace[0] + "\tat " + string + "\n");
						OriginsPaper.LOGGER
							.error("An unhandled exception occurred when ticking a Power! [{}]", var8.getClass().getSimpleName());
						String t = power.getType();
						if (t == null) {
							t = power.getId().toString();
						}

						OriginsPaper.LOGGER
							.error("Player: {} | Power: {} | CraftPower: {} | Throwable: {} {}", p.getName(), power.getTag(), t, var8.getMessage() == null ? var8.getClass().getSimpleName() : var8.getMessage(), stacktrace[0]);
					}
				}
			}
		}
	}

	private static final class Repeating implements Consumer<MinecraftServer> {
		public final int next;
		private final Consumer<MinecraftServer> task;
		private final IntPredicate requeue;

		private Repeating(Consumer<MinecraftServer> task, IntPredicate requeue, int interval) {
			this.task = task;
			this.requeue = requeue;
			this.next = interval;
		}

		public boolean shouldQueue(int predicate) {
			if (this.requeue == null)
				return true;
			return this.requeue.test(predicate);
		}


		@Override
		public void accept(MinecraftServer server) {
			this.task.accept(server);
		}
	}
}
