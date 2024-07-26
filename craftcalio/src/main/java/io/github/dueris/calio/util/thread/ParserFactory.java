package io.github.dueris.calio.util.thread;

import ca.spottedleaf.moonrise.common.util.TickThread;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ParserFactory implements ThreadFactory {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final AtomicInteger threadNumber;
	private final String namePrefix;

	public ParserFactory(int threadCount) {
		this.threadNumber = new AtomicInteger(threadCount);
		this.namePrefix = "CalioCompiler-";
	}

	@Override
	public Thread newThread(@NotNull Runnable runnable) {
		TickThread thread = new TickThread(runnable, this.namePrefix + this.threadNumber.getAndIncrement());
		thread.setUncaughtExceptionHandler((threadx, throwable) -> {
			LOGGER.error("Caught exception in tick-thread {} from {}", threadx, runnable);
			LOGGER.error("", throwable);
		});
		if (thread.getPriority() != 6) {
			thread.setPriority(6);
		}

		return thread;
	}
}
