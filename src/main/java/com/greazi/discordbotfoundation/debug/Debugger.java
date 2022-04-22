/*
 * Copyright (c) 2022. Greazi All rights reservered
 */

package com.greazi.discordbotfoundation.debug;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.SimpleBot;
import com.greazi.discordbotfoundation.settings.SimpleSettings;
import com.greazi.discordbotfoundation.utils.color.Color;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for solving problems and errors
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Debugger {

	/**
	 * Get if the given section is being debugged
	 * <p>
	 * You can set if the section is debugged by setting it in "Debug" key in your settings.yaml,
	 * by default your class extending {@link SimpleSettings}
	 * <p>
	 * If you set Debug to ["*"] this will always return true
	 *
	 * @param section
	 * @return
	 */
	public static boolean isDebugged(String section) {
		return SimpleSettings.getDebug().contains(section) || SimpleSettings.getDebug().contains("*");
	}

	/**
	 * Prints a debug messages to the console if the given section is being debugged
	 * <p>
	 * You can set if the section is debugged by setting it in "Debug" key in your settings.yaml,
	 * by default your class extending {@link SimpleSettings}
	 *
	 * @param section
	 * @param messages
	 */
	public static void debug(String section, String... messages) {
		if (isDebugged(section)) {
			for (final String message : messages)
				if (SimpleBot.hasInstance())
					Common.log("[" + Color.YELLOW + section + Color.RESET + "] " + message);
				else
					System.out.println("[" + Color.YELLOW + section + Color.RESET + "] " + message);
		}
	}

	/**
	 * Prints stack trace until we reach the native MC/Bukkit with a custom message
	 *
	 * @param message the message to wrap stack trace around
	 */
	public static void printStackTrace(String message) {
		final StackTraceElement[] trace = new Exception().getStackTrace();

		print("!----------------------------------------------------------------------------------------------------------!");
		print(message);
		print("!----------------------------------------------------------------------------------------------------------!");

		for (int i = 1; i < trace.length; i++) {
			final String line = trace[i].toString();

			if (canPrint(line))
				print("\tat " + line);
		}

		print("--------------------------------------------------------------------------------------------------------end-");
	}

	/**
	 * Prints a Throwable's first line and stack traces.
	 * <p>
	 * Ignores the native Bukkit/Minecraft server.
	 *
	 * @param throwable the throwable to print
	 */
	public static void printStackTrace(@NonNull Throwable throwable) {

		// Load all causes
		final List<Throwable> causes = new ArrayList<>();

		if (throwable.getCause() != null) {
			Throwable cause = throwable.getCause();

			do
				causes.add(cause);
			while ((cause = cause.getCause()) != null);
		}

		if (throwable instanceof FoException && !causes.isEmpty()) {
			// Do not print parent exception if we are only wrapping it, saves console spam
			print(throwable.getMessage());

		} else {
			print(throwable.toString());

			printStackTraceElements(throwable);
		}

		if (!causes.isEmpty()) {
			final Throwable lastCause = causes.get(causes.size() - 1);

			print(lastCause.toString());
			printStackTraceElements(lastCause);
		}
	}

	private static void printStackTraceElements(Throwable throwable) {
		for (final StackTraceElement element : throwable.getStackTrace()) {
			final String line = element.toString();

			if (canPrint(line))
				print("\tat " + line);
		}
	}

	/**
	 * Returns whether a line is suitable for printing as an error line - we ignore stuff from NMS and OBF as this is not needed
	 *
	 * @param message
	 * @return
	 */
	// TODO add a proper check if it can print the error
	private static boolean canPrint(String message) {
		return !message.contains("pindaas");
	}

	// Print a simple console message
	private static void print(String message) {
		if (SimpleBot.hasInstance())
			Common.logNoPrefix(message);
		else
			System.out.println(message);
	}
}
