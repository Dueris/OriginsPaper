package me.dueris.genesismc.util;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.util.holders.TriPair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AsyncTaskWorker {
    private static final ExecutorService service = Executors.newFixedThreadPool(2, new NamedTickThreadFactory("GenesisTaskWorker"));

    public static void submitTask(Runnable runnable) {
	service.submit(runnable);
    }

    public static void shutdown() {
	service.shutdown();
    }

    public static void submitTriPairConsumer(Consumer<TriPair> consumer, TriPair args) {
	service.submit(() -> consumer.accept(args));
    }

    public static void submitBiPairConsumer(Consumer<Pair> consumer, Pair args) {
	service.submit(() -> consumer.accept(args));
    }

    public static void submitConsumer(Consumer<Object> consumer, Object args) {
	service.submit(() -> consumer.accept(args));
    }
}
