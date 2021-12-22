package com.greazi.discordbotfoundation.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SimpleSettings {


	protected String getSettingsFileName() {
		return "settings.yml";
	}

	private static SimpleSettings instance;

	public static SimpleSettings getInstance(){
		if(instance == null){
			instance = new SimpleSettings();
		}

		return instance;
	}

	private JsonObject root;



	private SimpleSettings() {
		File file = new File(getSettingsFileName());

		if(!file.exists()){
			try {
				InputStream src = SimpleSettings.class.getResourceAsStream("/Settings.json");
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

	public boolean isConfigured(){
		return !getToken().isEmpty() || !getActivity().isEmpty();
	}

	public String getToken(){
		return root.get("token").getAsString();
	}

	public String getActivity(){
		return root.get("activity").getAsString();
	}

	public String getApiToken(){
		return root.get("apiToken").getAsString();
	}

	public String getSongodaApiToken(){
		return root.get("songodaApiToken").getAsString();
	}

	public String getMySqlHost(){
		return root.get("mySQL_host").getAsString();
	}

	public String getMySqlPort(){
		return root.get("mySQL_port").getAsString();
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

	public String getGithubToken(){
		return root.get("githubToken").getAsString();
	}

	public String getPteroUrl(){
		return root.get("pterodactylUrl").getAsString();
	}

	public String getPteroClientToken(){
		return root.get("pterodactylClientToken").getAsString();
	}

	public String getPteroApiToken(){
		return root.get("pterodactylApiToken").getAsString();
	}

	public String getPaypalClientID(){
		return root.get("PaypalClientID").getAsString();
	}
	public String getPaypalClientSecret(){
		return root.get("PaypalClientID").getAsString();
	}
}
