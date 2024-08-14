package io.github.dueris.originspaper.util;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.server.MinecraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class ApoliScheduler implements Listener {
	private final Int2ObjectMap<List<Consumer<MinecraftServer>>> taskQueue = new Int2ObjectOpenHashMap<>();
	private int currentTick = 0;

	@EventHandler
	public void tickEnd(ServerTickEndEvent e) {
		this.currentTick = OriginsPaper.server.getTickCount();
		List<Consumer<MinecraftServer>> runnables = this.taskQueue.remove(this.currentTick);
		if (runnables != null) for (int i = 0; i < runnables.size(); i++) {
			Consumer<MinecraftServer> runnable = runnables.get(i);
			runnable.accept(OriginsPaper.server);

			if (runnable instanceof Repeating repeating) {
				if (repeating.shouldQueue(this.currentTick))
					this.queue(runnable, ((Repeating) runnable).next);
			}
		}
	}

	/**
	 * queue a one time task to be executed on the server thread
	 *
	 * @param tick how many ticks in the future this should be called, where 0 means at the end of the current tick
	 * @param task the action to perform
	 */
	public void queue(Consumer<MinecraftServer> task, int tick) {
		this.taskQueue.computeIfAbsent(this.currentTick + tick + 1, t -> new ArrayList<>()).add(task);
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
