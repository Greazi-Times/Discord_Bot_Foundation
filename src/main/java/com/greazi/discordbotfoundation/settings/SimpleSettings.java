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
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class SimpleSettings {

	private static Map<String, Object> settings = Maps.newHashMap();
	private static final Map<String, Object> cachedSettings = Maps.newHashMap();

	public static void init() {
		File file = new File(Constants.File.SETTINGS);

		if(!file.exists()) {
			try {
				InputStream inputStream = SimpleSettings.class.getResourceAsStream(Constants.File.SETTINGS);
				Files.copy(inputStream, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
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
			if (i != keys.length - 1) {
				map = (Map<String, Object>) map.get(currentKey);
				finalKey.append(".");
			}else{
				cachedSettings.put(finalKey.toString(), map.get(currentKey));
				return map.get(currentKey);
			}
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

	// Default values
	public static String getName(){
		return getString("Name");
	}

	public static String getActivity(){
		return getString("Activity");
	}

	public static String getToken(){
		return getString("Token");
	}

	public static String getMainGuild(){
		return getString("MainGuild");
	}

	public static String getDebug(){
		return getString("Debug");
	}

	public static boolean getMysqlEnabled(){
		return getBoolean("Mysql.Enabled");
	}

	public static String getMysqlHost(){
		return getString("Mysql.Host");
	}

	public static int getMysqlPort(){
		return getInt("Mysql.Port");
	}

	public static String getMysqlDatabase(){
		return getString("Mysql.Database");
	}

	public static String getMysqlUsername(){
		return getString("Mysql.Username");
	}

	public static String getMysqlPassword(){
		return getString("Mysql.Password");
	}

	public static boolean getMysqlStoreMembers(){
		return getBoolean("Mysql.StoreMembers");
	}


}
