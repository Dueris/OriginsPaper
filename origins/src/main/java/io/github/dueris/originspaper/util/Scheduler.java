package io.github.dueris.originspaper.util;

import io.github.dueris.originspaper.client.MinecraftClient;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class Scheduler {
	public static Scheduler INSTANCE = new Scheduler();
	public static List<org.bukkit.entity.Player> delayedPlayers = new LinkedList<>();
	private final Int2ObjectMap<List<Task>> taskQueue = new Int2ObjectOpenHashMap<>();
	private int currentTick = 0;

	public static void tickAsyncScheduler() {
	}

	public void tick(@NotNull MinecraftServer m) {
		this.currentTick = m.getTickCount();
		List<Task> runnables = this.taskQueue.remove(this.currentTick);
		if (runnables != null) for (Task runnable : runnables) {
			runnable.accept(m);

			if (runnable instanceof Repeating repeating) { // reschedule repeating tasks
				if (repeating.shouldQueue(this.currentTick))
					this.queue(runnable, ((Repeating) runnable).next);
			}
		}

		for (ServerPlayer player : m.getPlayerList().getPlayers()) {
			PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);

			if (component == null) {
				continue;
			}

			component.serverTick();
			MinecraftClient.of(player).tick();
		}

	}

	/**
	 * queue a one time task to be executed on the server thread
	 *
	 * @param tick how many ticks in the future this should be called, where 0 means at the end of the current tick
	 * @param task the action to perform
	 */
	public void queue(Task task, int tick) {
		this.taskQueue.computeIfAbsent(this.currentTick + tick + 1, t -> new LinkedList<>()).add(task);
	}

	/**
	 * schedule a repeating task that is executed infinitely every n ticks
	 *
	 * @param task     the action to perform
	 * @param tick     how many ticks in the future this event should first be called
	 * @param interval the number of ticks in between each execution
	 */
	public void repeating(Task task, int tick, int interval) {
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
	public void repeatWhile(Task task, IntPredicate requeue, int tick, int interval) {
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
	public void repeatN(Task task, int times, int tick, int interval) {
		this.repeatWhile(task, new IntPredicate() {
			private int remaining = times;

			@Override
			public boolean test(int value) {
				return this.remaining-- > 0;
			}
		}, tick, interval);
	}

	public interface Task extends Consumer<MinecraftServer> {
		AtomicBoolean canceled = new AtomicBoolean(false);

		default void cancel() {
			canceled.set(true);
		}

		default boolean isCanceled() {
			return canceled.get();
		}
	}

	private static final class Repeating implements Task {
		public final int next;
		private final Task task;
		private IntPredicate requeue;

		private Repeating(Task task, IntPredicate requeue, int interval) {
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
			if (isCanceled()) {
				this.requeue = (m) -> false;
			} else {
				this.task.accept(server);
			}
		}
	}
}
