package com.greazi.discordbotfoundation.commons.waiter;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.internal.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <p>The EventWaiter is capable of handling specialized forms of
 * {@link net.dv8tion.jda.api.events.Event Event} that must meet criteria not normally specifiable
 * without implementation of an {@link net.dv8tion.jda.api.hooks.EventListener EventListener}.
 *
 * <p>Creating an EventWaiter requires provision and/or creation of a
 * {@link ScheduledExecutorService Executor}, and thus a proper
 * shutdown of said executor. The default constructor for an EventWaiter sets up a
 * working, "live", EventWaiter whose shutdown is triggered via JDA firing a
 * {@link net.dv8tion.jda.api.events.session.ShutdownEvent ShutdownEvent}.
 * <br>A more "shutdown adaptable" constructor allows the provision of a
 * {@code ScheduledExecutorService} and a choice of how exactly shutdown will be handled
 * (see {@link EventWaiter#EventWaiter(ScheduledExecutorService, boolean)} for more details).
 *
 * <p>As a final note, if you intend to use the EventWaiter, it is highly recommended you <b>DO NOT</b>
 * create multiple EventWaiters! Doing this will cause unnecessary increases in memory usage.
 */
public class EventWaiter implements EventListener {
    private static final Logger LOG = LoggerFactory.getLogger(EventWaiter.class);
    private final HashMap<Class<?>, Set<WaitingEvent>> waitingEvents;
    private final ScheduledExecutorService threadpool;
    private final boolean shutdownAutomatically;

    /**
     * Constructs an empty EventWaiter.
     */
    public EventWaiter() {
        this(Executors.newSingleThreadScheduledExecutor(), true);
    }

    /**
     * Constructs an EventWaiter using the provided {@link ScheduledExecutorService Executor}
     * as it's threadpool.
     *
     * <p>A developer might choose to use this constructor over the {@link EventWaiter#EventWaiter() default},
     * for using a alternate form of threadpool, as opposed to a {@link Executors#newSingleThreadExecutor() single thread executor}.
     * <br>A developer might also favor this over the default as they use the same waiter for multiple
     * shards, and thus shutdown must be handled externally if a special shutdown sequence is being used.
     *
     * <p>{@code shutdownAutomatically} is required to be manually specified by developers as a way of
     * verifying a contract that the developer will conform to the behavior of the newly generated EventWaiter:
     * <ul>
     *     <li>If {@code true}, shutdown is handled when a {@link net.dv8tion.jda.api.events.session.ShutdownEvent ShutdownEvent}
     *     is fired. This means that any external functions of the provided Executor is now impossible and any externally
     *     queued tasks are lost if they have yet to be run.</li>
     *     <li>If {@code false}, shutdown is now placed as a responsibility of the developer, and no attempt will be
     *     made to shutdown the provided Executor.</li>
     * </ul>
     * It's worth noting that this EventWaiter can serve as a delegate to invoke the threadpool's shutdown via
     * a call to {@link EventWaiter#shutdown() EventWaiter#shutdown()}.
     * However, this operation is only supported for EventWaiters that are not supposed to shutdown automatically,
     * otherwise invocation of {@code EventWaiter#shutdown()} will result in an
     * {@link UnsupportedOperationException UnsupportedOperationException}.
     *
     * @param threadpool            The ScheduledExecutorService to use for this EventWaiter's threadpool.
     * @param shutdownAutomatically Whether or not the {@code threadpool} will shutdown automatically when a
     *                              {@link net.dv8tion.jda.api.events.session.ShutdownEvent ShutdownEvent} is fired.
     * @throws IllegalArgumentException If the threadpool provided is {@code null} or
     *                                  {@link ScheduledExecutorService#isShutdown() is shutdown}
     * @see EventWaiter#shutdown() EventWaiter#shutdown()
     */
    public EventWaiter(final ScheduledExecutorService threadpool, final boolean shutdownAutomatically) {
        Checks.notNull(threadpool, "ScheduledExecutorService");
        Checks.check(!threadpool.isShutdown(), "Cannot construct EventWaiter with a closed ScheduledExecutorService!");

        this.waitingEvents = new HashMap<>();
        this.threadpool = threadpool;

        // "Why is there no default constructor?"
        //
        // When a developer uses this constructor we want them to be aware that this
        // is putting the task on them to shut down the threadpool if they set this to false,
        // or to avoid errors being thrown when ShutdownEvent is fired if they set it true.
        //
        // It is YOUR fault if you have a rogue threadpool that doesn't shut down if you
        // forget to dispose of it and set this false, or that certain tasks may fail
        // if you use this executor for other things and set this true.
        //
        // NOT MINE
        this.shutdownAutomatically = shutdownAutomatically;
    }

    /**
     * Gets whether the EventWaiter's internal ScheduledExecutorService
     * {@link ScheduledExecutorService#isShutdown() is shutdown}.
     *
     * @return {@code true} if the ScheduledExecutorService is shutdown, {@code false} otherwise.
     */
    public boolean isShutdown() {
        return threadpool.isShutdown();
    }

    /**
     * Waits an indefinite amount of time for an {@link net.dv8tion.jda.api.events.Event Event} that
     * returns {@code true} when tested with the provided {@link Predicate Predicate}.
     *
     * <p>When this occurs, the provided {@link Consumer Consumer} will accept and
     * execute using the same Event.
     *
     * @param <T>       The type of Event to wait for.
     * @param classType The {@link Class} of the Event to wait for. Never null.
     * @param condition The Predicate to test when Events of the provided type are thrown. Never null.
     * @param action    The Consumer to perform an action when the condition Predicate returns {@code true}. Never null.
     * @throws IllegalArgumentException One of two reasons:
     *                                  <ul>
     *                                      <li>1) Either the {@code classType}, {@code condition}, or {@code action} was {@code null}.</li>
     *                                      <li>2) The internal threadpool is shut down, meaning that no more tasks can be submitted.</li>
     *                                  </ul>
     */
    public <T extends Event> void waitForEvent(final Class<T> classType, final Predicate<T> condition, final Consumer<T> action) {
        waitForEvent(classType, condition, action, -1, null, null);
    }

    /**
     * Waits a predetermined amount of time for an {@link net.dv8tion.jda.api.events.Event Event} that
     * returns {@code true} when tested with the provided {@link Predicate Predicate}.
     *
     * <p>Once started, there are two possible outcomes:
     * <ul>
     *     <li>The correct Event occurs within the time allotted, and the provided
     *     {@link Consumer Consumer} will accept and execute using the same Event.</li>
     *
     *     <li>The time limit is elapsed and the provided {@link Runnable} is executed.</li>
     * </ul>
     *
     * @param <T>           The type of Event to wait for.
     * @param classType     The {@link Class} of the Event to wait for. Never null.
     * @param condition     The Predicate to test when Events of the provided type are thrown. Never null.
     * @param action        The Consumer to perform an action when the condition Predicate returns {@code true}. Never null.
     * @param timeout       The maximum amount of time to wait for, or {@code -1} if there is no timeout.
     * @param unit          The {@link TimeUnit TimeUnit} measurement of the timeout, or
     *                      {@code null} if there is no timeout.
     * @param timeoutAction The Runnable to run if the time runs out before a correct Event is thrown, or
     *                      {@code null} if there is no action on timeout.
     * @throws IllegalArgumentException One of two reasons:
     *                                  <ul>
     *                                      <li>1) Either the {@code classType}, {@code condition}, or {@code action} was {@code null}.</li>
     *                                      <li>2) The internal threadpool is shut down, meaning that no more tasks can be submitted.</li>
     *                                  </ul>
     */
    public <T extends Event> void waitForEvent(final Class<T> classType, final Predicate<T> condition, final Consumer<T> action,
                                               final long timeout, final TimeUnit unit, final Runnable timeoutAction) {
        Checks.check(!isShutdown(), "Attempted to register a WaitingEvent while the EventWaiter's threadpool was already shut down!");
        Checks.notNull(classType, "The provided class type");
        Checks.notNull(condition, "The provided condition predicate");
        Checks.notNull(action, "The provided action consumer");

        final WaitingEvent we = new WaitingEvent<>(condition, action);
        final Set<WaitingEvent> set = waitingEvents.computeIfAbsent(classType, c -> ConcurrentHashMap.newKeySet());
        set.add(we);

        if (timeout > 0 && unit != null) {
            threadpool.schedule(() ->
            {
                try {
                    if (set.remove(we) && timeoutAction != null)
                        timeoutAction.run();
                } catch (final Exception ex) {
                    LOG.error("Failed to run timeoutAction", ex);
                }
            }, timeout, unit);
        }
    }

    @Override
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public final void onEvent(final GenericEvent event) {
        Class c = event.getClass();

        // Runs at least once for the fired Event, at most
        // once for each superclass (excluding Object) because
        // Class#getSuperclass() returns null when the superclass
        // is primitive, void, or (in this case) Object.
        while (c != null) {
            final Set<WaitingEvent> set = waitingEvents.get(c);
            if (set != null) {
                // WaitingEvent#attempt invocations that return true have passed their condition tests
                // and executed the action. We remove the ones that have successfully ran (those that returns true)
                set.removeIf(wEvent -> wEvent.attempt(event));
            }
            if (event instanceof ShutdownEvent && shutdownAutomatically) {
                threadpool.shutdown();
            }
            c = c.getSuperclass();
        }
    }

    /**
     * Closes this EventWaiter if it doesn't normally shutdown automatically.
     *
     * <p><b>IF YOU USED THE DEFAULT CONSTRUCTOR WITH NO ARGUMENTS DO NOT CALL THIS!</b>
     * <br>Calling this method on an EventWaiter that does shutdown automatically will result in
     * an {@link UnsupportedOperationException UnsupportedOperationException} being thrown.
     *
     * @throws UnsupportedOperationException The EventWaiter is supposed to close automatically.
     */
    public void shutdown() {
        if (shutdownAutomatically)
            throw new UnsupportedOperationException("Shutting down EventWaiters that are set to automatically close is unsupported!");

        threadpool.shutdown();
    }

    private class WaitingEvent<T extends GenericEvent> {
        final Predicate<T> condition;
        final Consumer<T> action;

        WaitingEvent(final Predicate<T> condition, final Consumer<T> action) {
            this.condition = condition;
            this.action = action;
        }

        boolean attempt(final T event) {
            if (condition.test(event)) {
                action.accept(event);
                return true;
            }
            return false;
        }
    }
}
