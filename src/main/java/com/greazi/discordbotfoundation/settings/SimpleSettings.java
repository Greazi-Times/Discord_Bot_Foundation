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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The main settings class that handles the whole settings system
 */
public class SimpleSettings {

    // Maps of all the settings and settings cache
    private static Map<String, Object> settings = Maps.newHashMap();
    private static final Map<String, Object> cachedSettings = Maps.newHashMap();

    /**
     * The main method that calls the file and sets the file
     */
    public static void init() {
        // The settings file
        final File file = new File(Constants.File.SETTINGS);

        // If the file doesn't exist it will create a new file
        if (!file.exists()) {
            try {
                new ResourceCopier().saveResource(Constants.File.SETTINGS);
            } catch (final Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        // Set up the Yaml handler
        try {
            final InputStream inputStream = Files.newInputStream(file.toPath());
            final Yaml yaml = new Yaml();
            settings = yaml.load(inputStream);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve an Object from the settings
     *
     * @param key The settings path
     * @return The settings value
     */
    private static Object getObject(final String key) {
        if (cachedSettings.containsKey(key)) {
            return cachedSettings.get(key);
        }
        if (!key.contains(".")) {
            if (!settings.containsKey(key)) {
                return "Unknown key";
            }
            final Object returnValue = settings.get(key);
            cachedSettings.put(key, returnValue);
            return returnValue;
        }

        Map<String, Object> map = settings;
        final String[] keys = key.split("\\.");
        final StringBuilder finalKey = new StringBuilder();

        for (int i = 0; i < keys.length; i++) {
            final String currentKey = keys[i];
            if (!map.containsKey(currentKey)) {
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

    /**
     * Retrieve a String from the settings file
     *
     * @param key The settings path
     * @return The settings value
     */
    protected static String getString(final String key) {
        final Object returnValue = getObject(key);
        return String.valueOf(returnValue);
    }

    protected static List<String> getStringList(final String key) {
        final Object returnValue = getObject(key);
        return (List<String>) returnValue;
    }

    /**
     * Retrieve a Boolean from the settings file
     *
     * @param key The settings path
     * @return The settings value
     */
    protected static boolean getBoolean(final String key) {
        final String returnValue = getString(key);
        if (returnValue.equalsIgnoreCase("true") || returnValue.equalsIgnoreCase("false") || returnValue.equalsIgnoreCase("1") || returnValue.equalsIgnoreCase("0")) {
            return Boolean.parseBoolean(returnValue);
        } else {
            Common.error("Value from key: " + key + ", Is not of type boolean!!");
            return false;
        }
    }

    /**
     * Retrieve an int from the settings file
     *
     * @param key The settings path
     * @return The settings value
     */
    protected static int getInt(final String key) {
        final String returnValue = getString(key);
        try {
            return Integer.parseInt(returnValue);
        } catch (final NumberFormatException e) {
            Common.error("Value from key: " + key + ", Is not of type int!!");
            return 0;
        }
    }

    /**
     * Retrieve a Double from the settings file
     *
     * @param key The settings path
     * @return The settings value
     */
    protected static double getDouble(final String key) {
        final String returnValue = getString(key);
        try {
            return Double.parseDouble(returnValue);
        } catch (NumberFormatException | NullPointerException e) {
            Common.error("Value from key: " + key + ", Is not of type double!!");
            return 0;
        }
    }

    /**
     * Retrieve a Long from the settings file
     *
     * @param key The settings path
     * @return The settings value
     */
    protected static long getLong(final String key) {
        final String returnValue = getString(key);
        try {
            return Long.parseLong(returnValue);
        } catch (final NumberFormatException e) {
            Common.error("Value from key: " + key + ", Is not of type long!!");
            return 0;
        }
    }

    /**
     * Retrieve a ArrayList from the settings file
     *
     * @param key The settings path
     * @return The settings value
     */
    protected static ArrayList<String> getArray(final String key) {
        final Object returnValue = getObject(key);
        if (returnValue instanceof ArrayList) {
            return (ArrayList<String>) returnValue;
        } else {
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
        public static long MainGuild() {
            return getLong(path + "MainGuild");
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
            return getString(path + "Type");
        }

        // The message for the activity
        public static String Message() {
            return getString(path + "Message");
        }
    }

    /**
     * Retrieve all the developer settings
     */
    public static class Developer {

        // Set the main path of the settings values
        private static final String path = "Developer.";

        // The Name
        public static String Name() {
            return getString(path + "Name");
        }

        // The Website
        public static String Website() {
            return getString(path + "Website");
        }

        // The Image
        public static String Image() {
            return getString(path + "Image");
        }
    }

    /**
     * Retrieve all the embed settings
     */
    public static class Embed {

        // Set the main path of the settings values
        private static final String path = "Embed.";

        // The link
        public static String Link() {
            return getString(path + "Link");
        }

        // The footer
        public static String Footer() {
            return getString(path + "Footer");
        }

        public static class Image {

            // The author image
            public static String Author() {
                return getString(path + "Image.Author");
            }

            // The footer image
            public static String Footer() {
                return getString(path + "Image.Footer");
            }
        }
    }

    /**
     * Retrieve all the console settings
     */
    public static class Console {

        // Set the main path of the settings values
        private static final String consolePath = "Console.";

        public static class Commands {

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

    public static class Stop {

        private static final String stopPath = "Commands.Stop.";

        public static boolean Enabled() {
            return getBoolean(stopPath + "Enabled");
        }

        public static ArrayList<String> AllowedRoles() {
            return getArray(stopPath + "AllowedRoles").stream().map(String::toLowerCase).collect(Collectors.toCollection(ArrayList::new));
        }
    }

    /**
     * Retrieve all the database settings
     */
    public static class Database {

        // Set the main path of the settings values
        private static final String path = "Database.";

        // Returns if the database system is enabled
        public static boolean Enabled() {
            return getBoolean(path + "Enabled");
        }

        // The database host
        public static String Host() {
            return getString(path + "Host");
        }

        // The database port
        public static int Port() {
            return getInt(path + "Port");
        }

        // The database name
        public static String Database() {
            return getString(path + "Database");
        }

        // The database username
        public static String Username() {
            return getString(path + "Username");
        }

        // The database password
        public static String Password() {
            return getString(path + "Password");
        }

        public static String Link() {
            return getString(path + "Link");
        }
    }

    /**
     * Retrieve all debug sections that are enabled
     */
    public static String Debug() {
        return getString("Debug");
    }
}
