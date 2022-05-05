/*
 * Copyright (c) 2021 - 2022. Greazi - All rights reservered
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.greazi.discordbotfoundation.settings;

import com.google.common.collect.Maps;
import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.constants.Constants;
import com.greazi.discordbotfoundation.utils.ResourceCopier;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleSettings {

	private static Map<String, Object> settings = Maps.newHashMap();
	private static final Map<String, Object> cachedSettings = Maps.newHashMap();

	public static void init() {
		File file = new File(Constants.File.SETTINGS);

		if(!file.exists()) {
			try{
				new ResourceCopier().saveResource(Constants.File.SETTINGS);
			}catch (Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}

		try {
			InputStream inputStream = Files.newInputStream(file.toPath());
			Yaml yaml = new Yaml();
			settings = yaml.load(inputStream);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private static Object getObject(String key) {
		if (cachedSettings.containsKey(key)) {
			return cachedSettings.get(key);
		}
		if (!key.contains(".")) {
			if(!settings.containsKey(key)) {
				return "Unknown key";
			}
			Object returnValue = settings.get(key);
			cachedSettings.put(key, returnValue);
			return returnValue;
		}

		Map<String, Object> map = settings;
		String[] keys = key.split("\\.");
		StringBuilder finalKey = new StringBuilder();

		for (int i = 0; i < keys.length; i++) {
			String currentKey = keys[i];
			if(!map.containsKey(currentKey)) {
				return "Unknown key";
			}

			finalKey.append(currentKey);
			if (i == keys.length - 1) {
				cachedSettings.put(finalKey.toString(), map.get(currentKey));
				return map.get(currentKey);
			}

			map = (Map<String, Object>) map.get(currentKey);
			finalKey.append(".");
		}
		return "Unknown error";
	}

	protected static String getString(String key) {
		Object returnValue = getObject(key);
		return String.valueOf(returnValue);
	}

	protected static boolean getBoolean(String key) {
		String returnValue = getString(key);
		if(returnValue.equalsIgnoreCase("true") || returnValue.equalsIgnoreCase("false") || returnValue.equalsIgnoreCase("1") || returnValue.equalsIgnoreCase("0")) {
			return Boolean.parseBoolean(returnValue);
		}else{
			Common.error("Value from key: " + key + ", Is not of type boolean!!");
			return false;
		}
	}

	protected static int getInt(String key) {
		String returnValue = getString(key);
		try {
			return Integer.parseInt(returnValue);
		} catch (NumberFormatException e) {
			Common.error("Value from key: " + key + ", Is not of type int!!");
			return 0;
		}
	}

	protected static double getDouble(String key) {
		String returnValue = getString(key);
		try {
			return Double.parseDouble(returnValue);
		} catch (NumberFormatException | NullPointerException e) {
			Common.error("Value from key: " + key + ", Is not of type double!!");
			return 0;
		}
	}

	protected static long getLong(String key) {
		String returnValue = getString(key);
		try {
			return Long.parseLong(returnValue);
		} catch (NumberFormatException e) {
			Common.error("Value from key: " + key + ", Is not of type long!!");
			return 0;
		}
	}

	protected static ArrayList<String> getArray(String key) {
		Object returnValue = getObject(key);
		if(returnValue instanceof ArrayList) {
			return (ArrayList<String>) returnValue;
		}else{
			Common.error("Value from key: " + key + ", Is not of type List!!");
			return new ArrayList<>();
		}
	}

	// ----------------------------------------------------------------------------------------
	// Main settings
	// ----------------------------------------------------------------------------------------

	/**
	 * Retrieve all the bot settings
	 */
	public static class Bot {

		// Set the main path of the settings values
		private static final String path = "Bot.";

		// The token for the bot
		public static String Token() {
			return getString(path + "Token");
		}

		// The main guild of the bot
		public static String MainGuild() {
			return getString(path + "MainGuild");
		}

		// The name of the bot
		public static String Name() {
			return getString(path + "Name");
		}
	}

	/**
	 * Retrieve all the activity settings
	 */
	public static class Activity {

		// Set the main path of the settings values
		private static final String path = "Activity.";

		// The activity type
		public static String Type() {
			return getString(path + "Activity.Type");
		}

		// The message for the activity
		public static String Message() {
			return getString(path + "Activity.Message");
		}
	}

	/**
	 * Retrieve all the console settings
	 */
	public static class Console {

		// Set the main path of the settings values
		private static final String consolePath = "Console.";

		public static class Commands{

			// Set the main path of the settings values
			private static final String commandsPath = "Commands.";

			// If console commands are enabled
			public static boolean Enabled() {
				return getBoolean(consolePath + commandsPath + "Enabled");
			}

			// A list of disabled commands
			public static ArrayList<String> Disabled() {
				return getArray(consolePath + commandsPath + "Disabled").stream().map(String::toLowerCase).collect(Collectors.toCollection(ArrayList::new));
			}
		}
	}

	/**
	 * Retrieve all the database settings
	 */
	public static class Database {

		// Set the main path of the settings values
		private static final String path = "Database.";

		// Returns if the database system is enabled
		public static boolean Enabled(){
			return getBoolean(path + "Enabled");
		}

		// The database host
		public static String Host(){
			return getString(path + "Host");
		}

		// The database port
		public static int Port(){
			return getInt(path + "Port");
		}

		// The database name
		public static String Database(){
			return getString(path + "Database");
		}

		// The database username
		public static String Username(){
			return getString(path + "Username");
		}

		// The database password
		public static String Password(){
			return getString(path + "Password");
		}
	}

	/**
	 * Retrieve all debug sections that are enabled
	 */
	public static String Debug(){
		return getString("Debug");
	}
}
