package me.dueris.genesismc.util;

import com.mojang.logging.LogUtils;
import io.papermc.paper.util.TickThread;
import org.slf4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedTickThreadFactory implements ThreadFactory { // Extends the functionality of the NamedThreadFactory by vanilla
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private int priority;
    private boolean daemon;

    public NamedTickThreadFactory(String name) {
        SecurityManager securityManager = System.getSecurityManager();
        this.group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = name + "-";
        this.priority = 5;
        this.daemon = false;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        TickThread thread = new TickThread(runnable, namePrefix + this.threadNumber.getAndIncrement());
        thread.setDaemon(daemon);
        thread.setUncaughtExceptionHandler((threadx, throwable) -> {
            LOGGER.error("Caught exception in thread {} from {}", threadx, runnable);
            LOGGER.error("", throwable);
        });
        if (thread.getPriority() != priority) {
            thread.setPriority(priority);
        }

        return thread;
    }
}
