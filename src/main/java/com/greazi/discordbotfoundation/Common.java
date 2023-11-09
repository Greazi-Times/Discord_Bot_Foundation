package com.greazi.discordbotfoundation;

import lombok.Getter;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class holds some pre-made modules
 */
public final class Common {

    // ------------------------------------------------------------------------------------------------------------
    // Aesthetics
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Used to send messages to the console without repetition, e.g. if they attempt to break a block
     * in a restricted region, we will not spam their chat with "You cannot break this block here" 120x times,
     * instead, we only send this message once per X seconds. This cache holds the last times when we
     * sent that message so we know how long to wait before the next one.
     */
    private static final Map<String, Long> TIMED_LOG_CACHE = new HashMap<>();

    /**
     * Should we add a prefix to the messages we send to the console?
     * <p>
     * True by default
     */
    public static boolean ADD_LOG_PREFIX = true;

    /**
     * The log prefix applied on log() methods
     */
    @Getter
    private static String logPrefix = "[INFO]";

    /**
     * Set the log prefix applied for messages in the console from log() methods.
     * <p>
     * Default output is [INFO]
     *
     * @param prefix
     */
    public static void setLogPrefix(final String prefix) {
        logPrefix = prefix;
    }

    /**
     * Returns a long ------ console line
     *
     * @return
     */
    public static String consoleLine() {
        return "!-----------------------------------------------------!";
    }

    /**
     * Returns a long ______ console line
     *
     * @return
     */
    public static String consoleLineSmooth() {
        return "______________________________________________________________";
    }

    /**
     * Returns a very long -------- config line
     *
     * @return
     */
    public static String configLine() {
        return "-------------------------------------------------------------------------------------------";
    }

    // ------------------------------------------------------------------------------------------------------------
    // Logging and error handling
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Logs the message, and saves the time it was logged. If you call this method
     * to log exactly the same message within the delay in seconds, it will not be logged.
     * <p>
     * Saves console spam.
     *
     * @param delaySec
     * @param msg
     */
    public static void logTimed(final int delaySec, final String msg) {
        if (!TIMED_LOG_CACHE.containsKey(msg)) {
            log(msg);
            TIMED_LOG_CACHE.put(msg, TimeUtil.currentTimeSeconds());
            return;
        }

        if (TimeUtil.currentTimeSeconds() - TIMED_LOG_CACHE.get(msg) > delaySec) {
            log(msg);
            TIMED_LOG_CACHE.put(msg, TimeUtil.currentTimeSeconds());
        }
    }

    /**
     * A dummy helper method adding "Warning: " to the given message
     * and logging it.
     *
     * @param message
     */
    public static void success(final String message) {
        logNoPrefix(ConsoleColor.GREEN + "[SUCCESS] " + message);
    }

    /**
     * A dummy helper method adding "Warning: " to the given message
     * and logging it.
     *
     * @param message
     */
    public static void warning(final String message) {
        logNoPrefix(ConsoleColor.RED + "[WARNING] " + message);
    }

    /**
     * Logs a bunch of messages to the console
     *
     * @param messages
     */
    public static void log(final List<String> messages) {
        log(toArray(messages));
    }

    /**
     * Logs a bunch of messages to the console
     *
     * @param messages
     */
    public static void log(final String... messages) {
        log(true, messages);
    }

    /**
     * The main logging method. Logs the given messages to the console.
     *
     * @param addLogPrefix Should we add the log prefix to the messages?
     * @param messages     The messages to log
     */
    private static void log(final boolean addLogPrefix, final String... messages) {
        if (messages == null)
            return;

        for (final String message : messages) {
            if (message.equals("none"))
                continue;

            for (final String part : splitNewline(message)) {

                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                final LocalTime localTime = LocalTime.now();

                if (addLogPrefix && ADD_LOG_PREFIX) {
                    System.out.println(ConsoleColor.WHITE + dtf.format(localTime) + ConsoleColor.BLACK_BRIGHT + " " + ConsoleColor.GREEN + logPrefix + ConsoleColor.RESET + " " + part + ConsoleColor.RESET);
                    saveToFile("[" + dtf.format(localTime) + "] " + logPrefix + " " + part);
                } else {
                    System.out.println(ConsoleColor.WHITE + dtf.format(localTime) + ConsoleColor.BLACK_BRIGHT + " " + ConsoleColor.RESET + part + ConsoleColor.RESET);
                    saveToFile("[" + dtf.format(localTime) + "] " + part);
                }
            }
        }
    }

    /**
     * Logs a bunch of messages to the console
     * <p>
     * Does not add {@link #getLogPrefix()}
     *
     * @param messages
     */
    public static void logNoPrefix(final String... messages) {
        log(false, messages);
    }

    /**
     * Logs a bunch of messages to the console in a {@link #consoleLine()} frame.
     *
     * @param messages
     */
    public static void logFramed(final String... messages) {
        logFramed(false, messages);
    }

    /**
     * Logs a bunch of messages to the console in a {@link #consoleLine()} frame.
     * <p>
     * Used when an error occurs, can also disable the bot
     *
     * @param disableBot
     * @param messages
     */
    public static void logFramed(final boolean disableBot, final String... messages) {
        if (messages != null && !Valid.isNullOrEmpty(messages)) {
            logNoPrefix(ConsoleColor.BLACK_BRIGHT + consoleLine());

            for (final String msg : messages)
                logNoPrefix(msg + ConsoleColor.RESET);

            logNoPrefix(ConsoleColor.BLACK_BRIGHT + consoleLine());
        }
    }

    /**
     * Saves the error, prints the stack trace and logs it in frame.
     * Possible to use %error variable
     *
     * @param messages
     */
    public static void error(final String... messages) {
        logFramed(ConsoleColor.RED + Arrays.toString(messages) + ConsoleColor.RESET);
    }

    /**
     * Logs the messages in frame (if not null),
     * saves the error to errors.log and then throws it
     * <p>
     * Possible to use %error variable
     *
     * @param t
     * @param messages
     */
    public static void throwError(Throwable t, final String... messages) {

        // Get to the root cause of this problem
        while (t.getCause() != null)
            t = t.getCause();

        // Delegate to only print out the relevant stuff
        if (t instanceof BotException)
            throw (BotException) t;

        if (messages != null)
            logFramed(false, replaceErrorVariable(t, messages));

        sneaky(t);

    }

    /*
     * Replace the %error variable with a smart error info, see above
     */
    private static String[] replaceErrorVariable(Throwable throwable, final String... msgs) {
        while (throwable.getCause() != null)
            throwable = throwable.getCause();

        final String throwableName = throwable.getClass().getSimpleName();
        final String throwableMessage = throwable.getMessage() == null || throwable.getMessage().isEmpty() ? "" : ": " + throwable.getMessage();

        for (int i = 0; i < msgs.length; i++) {
            final String error = throwableName + throwableMessage;

            msgs[i] = msgs[i]
                    .replace("%error%", error)
                    .replace("%error", error);
        }

        return msgs;
    }

    public static void dm(@NotNull final User user, @NotNull final String message) {
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue(message1 -> {
        }, error -> {
            throwError(error, "Failed to send message to " + user.getAsTag() + " (" + user.getId() + ") This user has either blocked the bot or disabled dm's from non-friends.");
        }));
    }

    public static void dm(@NotNull final User user, @NotNull final SimpleEmbedBuilder embed) {
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessageEmbeds(embed.build()).queue(message -> {
        }, error -> {
            throwError(error, "Failed to send message to " + user.getAsTag() + " (" + user.getId() + ") This user has either blocked the bot or disabled dm's from non-friends.");
        }));
    }

    // ------------------------------------------------------------------------------------------------------------
    // Rare message handling
    // ------------------------------------------------------------------------------------------------------------

    /**
     * Converts a list of string into a string array
     *
     * @param array
     * @return
     */
    public static String[] toArray(final Collection<String> array) {
        return array == null ? new String[0] : array.toArray(new String[array.size()]);
    }

    /**
     * Creates a new modifiable array list from array
     *
     * @param array
     * @return
     */
    public static <T> ArrayList<T> toList(final T... array) {
        return array == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(array));
    }

    /**
     * Converts {@link Iterable} to {@link List}
     *
     * @param it the iterable
     * @return the new list
     */
    public static <T> List<T> toList(final Iterable<T> it) {
        final List<T> list = new ArrayList<>();

        if (it != null)
            it.forEach(el -> {
                if (el != null)
                    list.add(el);
            });

        return list;
    }

    /**
     * Return a new hashmap having the given first key and value pair
     *
     * @param <A>
     * @param <B>
     * @param firstKey
     * @param firstValue
     * @return
     */
    public static <A, B> Map<A, B> newHashMap(final A firstKey, final B firstValue) {
        final Map<A, B> map = new HashMap<>();
        map.put(firstKey, firstValue);

        return map;
    }

    /**
     * Create a new hashset
     *
     * @param <T>
     * @param keys
     * @return
     */
    public static <T> Set<T> newSet(final T... keys) {
        return new HashSet<>(Arrays.asList(keys));
    }

    /**
     * Create a new array list that is mutable
     *
     * @param <T>
     * @param keys
     * @return
     */
    public static <T> List<T> newList(final T... keys) {
        final List<T> list = new ArrayList<>();

        for (final T key : keys)
            list.add(key);

        return list;
    }

    /**
     * Converts an unchecked exception into checked
     *
     * @param throwable
     */
    public static void sneaky(final Throwable throwable) {
        try {
            SneakyThrow.sneaky(throwable);

        } catch (final NoClassDefFoundError | NoSuchFieldError | NoSuchMethodError err) {
            throw new BotException(throwable);
        }
    }

    /**
     * Attempts to split the message using the \n character. This is used in some bots
     * since some OS's have a different method for splitting so we just go letter by letter
     * there and match \ and n and then split it.
     *
     * @param message
     * @return
     * @deprecated usage specific, also some operating systems seems to handle this poorly
     */
    @Deprecated
    public static String[] splitNewline(final String message) {
        if (!com.greazi.old.SimpleBot.getInstance().enforceNewLine())
            return message.split("\n");

        final String delimiter = "GREAZIWASHERE";

        final char[] chars = message.toCharArray();
        String parts = "";

        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];

            if ('\\' == c)
                if (i + 1 < chars.length)
                    if ('n' == chars[i + 1]) {
                        i++;

                        parts += delimiter;
                        continue;
                    }
            parts += c;
        }

        return parts.split(delimiter);
    }

    private static String getCurrentDateTime() {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final LocalDateTime now = LocalDateTime.now();
        return "<" + dtf.format(now) + "> ";
    }

    public static void saveToFile(final String message) {
        if (!SimpleSettings.Console.logConsole) return;

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final LocalDateTime now = LocalDateTime.now();
        final String currentDate = dtf.format(now);

        final String location = new File(com.greazi.old.SimpleBot.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();


        // Get the location of the .jar file
        String jarFilePath = SimpleBot.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // Decode the URL-encoded path (if needed)
        try {
            jarFilePath = java.net.URLDecoder.decode(jarFilePath, "UTF-8");
        } catch (final java.io.UnsupportedEncodingException ex) {
            // Handle the exception if decoding fails
            throwError(ex, "Failed to decode the .jar file path.");
        }

        // If the .jar is run from the command line, the path will include the .jar file name,
        // so we need to remove it to get the folder location.
        final File jarFile = new File(jarFilePath);
        final String jarFolder = jarFile.getParent();

        final String logPath = jarFolder + "/logs/";

        final File file = new File(logPath);

        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        try {
            final FileWriter fw = new FileWriter(logPath + "/" + currentDate + ".log", true);
            final BufferedWriter bw = new BufferedWriter(fw);
            bw.write(message.replaceAll("\u001B\\[[;\\d]*m", ""));
            bw.newLine();
            bw.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * A wrapper for sneaky error's
 */
class SneakyThrow {

    public static void sneaky(final Throwable t) {
        throw SneakyThrow.superSneaky(t);
    }

    private static <T extends Throwable> T superSneaky(final Throwable t) throws T {
        throw (T) t;
    }
}
