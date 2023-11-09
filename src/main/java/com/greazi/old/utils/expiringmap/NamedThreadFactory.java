package com.greazi.old.utils.expiringmap;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Named thread factory.
 */
public final class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String nameFormat;

    /**
     * Creates a thread factory that names threads according to the {@code nameFormat} by supplying a
     * single argument to the format representing the thread number.
     *
     * @param nameFormat
     */
    public NamedThreadFactory(final String nameFormat) {
        this.nameFormat = nameFormat;
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread = new Thread(r, String.format(this.nameFormat, this.threadNumber.getAndIncrement()));
        thread.setDaemon(true);
        return thread;
    }
}
