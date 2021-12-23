package com.greazi.discordbotfoundation.settings;

import com.greazi.discordbotfoundation.constants.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * SimpleJson is a tool that checks if the settings file is there,
 * As well as making some easy methodes to generate a proper settings file.
 */
public class SimpleJson {

	/**
	 * Check if the settings file exists.
	 * @return Boolean
	 */
	public static Boolean settingsFileExists() {
		return new File(Constants.File.SETTINGS).exists();
	}

	@SuppressWarnings("unchecked")
	public static void settingsFileCreate() {
		JSONObject generalSettings = new JSONObject();
		generalSettings.put("Token", "00000000000000000000000000000000000000000000000000000000000");
		generalSettings.put("Activity", "For help");
		generalSettings.put("Settings_Version", "1");

		JSONObject generalObjet = new JSONObject();
		generalObjet.put("General", generalSettings);
	}

	public static void settingsFileCheckVersion() {

	}


	public static void main( String[] args )
	{
		//First Employee
		JSONObject employeeDetails = new JSONObject();
		employeeDetails.put("firstName", "Lokesh");
		employeeDetails.put("lastName", "Gupta");
		employeeDetails.put("website", "howtodoinjava.com");

		JSONObject employeeObject = new JSONObject();
		employeeObject.put("employee", employeeDetails);

		//Second Employee
		JSONObject employeeDetails2 = new JSONObject();
		employeeDetails2.put("firstName", "Brian");
		employeeDetails2.put("lastName", "Schultz");
		employeeDetails2.put("website", "example.com");

		JSONObject employeeObject2 = new JSONObject();
		employeeObject2.put("employee", employeeDetails2);

		//Add employees to list
		JSONArray employeeList = new JSONArray();
		employeeList.add(employeeObject);
		employeeList.add(employeeObject2);

		//Write JSON file
		try (FileWriter file = new FileWriter("employees.json")) {
			//We can write any JSONArray or JSONObject instance to the file
			file.write(employeeList.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
