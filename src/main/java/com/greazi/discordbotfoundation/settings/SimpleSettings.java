package com.greazi.discordbotfoundation.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.greazi.discordbotfoundation.constants.Constants;
import okhttp3.internal.http2.Settings;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SimpleSettings {

	private static SimpleSettings instance;

	public static SimpleSettings getInstance() {
		if(instance == null){
			instance = new SimpleSettings();
		}

		return instance;
	}

	private JsonObject root;

	private SimpleSettings() {
		File file = new File("Settings.json");

		if(!file.exists()) {
			try {
				InputStream src = Settings.class.getResourceAsStream(Constants.File.SETTINGS);
				Files.copy(src, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

			JsonParser jsonParser = new JsonParser();
			root = (JsonObject) jsonParser.parse(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isSettingsConfigured() {
		return getToken().equals("") ||
				getName().equals("") ||
				getActivity().equals("") ||
				getMainGuild().equals("");
	}

	public String getToken() {
		return root.get("token").getAsString();
	}

	public String getName() {
		return root.get("name").getAsString();
	}

	public String getActivity() {
		return root.get("activity").getAsString();
	}
	
	public boolean isMysqlEnabled(){
		return !getMySqlHost().equals("") ||
				getMySqlPort() != 0 ||
				!getMySqlDatabase().equals("") ||
				!getMySqlUsername().equals("") ||
				!getMySqlPassword().equals("");
	}

	public String getMySqlHost(){
		return root.get("mySQL_host").getAsString();
	}

	public int getMySqlPort(){
		return root.get("mySQL_port").getAsInt();
	}

	public String getMySqlDatabase(){
		return root.get("mySQL_database").getAsString();
	}

	public String getMySqlUsername(){
		return root.get("mySQL_username").getAsString();
	}

	public String getMySqlPassword(){
		return root.get("mySQL_password").getAsString();
	}

	public Boolean isStoreMembersEnabled(){
		return root.get("mySQL_storeMembers").getAsBoolean();
	}

	public String getMainGuild() {
		return root.get("mainGuild").getAsString();
	}

	public String getDebug() {
		return root.get("debug").getAsString();
	}
}
