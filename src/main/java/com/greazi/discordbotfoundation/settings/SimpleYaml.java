package com.greazi.discordbotfoundation.settings;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.constants.Constants;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;


/**
 * SimpleJson is a tool that checks if the settings file is there,
 * As well as making some easy methods to generate a proper settings file.
 */
public class SimpleYaml {

	InputStream inputStream;
	Yaml yaml = new Yaml();

	/**
	 * A tool that checks if a file exists
	 * @param File The file that should be checked
	 * @return Boolean
	 */
	public static boolean fileExists(String File) {
		File tempFile = new File(File);

		boolean fileExists;
		fileExists = tempFile.exists();


		return fileExists;
	}

	/**
	 * Load configuration files
	 * @return
	 */
	public Object loadFile(String File) {
		InputStream inputStream = this.getClass()
				.getClassLoader()
				.getResourceAsStream(File);
		return yaml.load(inputStream);
	}

	public void displayError0(String File) {
		Common.log.error(
			"&4    ___                  _ ",
			"&4   / _ \\  ___  _ __  ___| |",
			"&4  | | | |/ _ \\| '_ \\/ __| |",
			"&4  | |_| | (_) | |_) \\__ \\_|",
			"&4   \\___/ \\___/| .__/|___(_)",
			"&4             |_|          ",
			"&4!-----------------------------------------------------!",
			" &cError loading File " + File + ", bot is disabled!",
			" &cRunning on Java " + System.getProperty("java.version"),
			"&4!-----------------------------------------------------!");
	}

	private InputStream getPreSettingsFile() {
		return getClass().getResourceAsStream( Constants.File.SETTINGS );
	}

	public void savePreSettingsFile() throws FileNotFoundException {
		Yaml yaml = new Yaml();
		Map<String , Object> yamlMaps;

		inputStream = getClass().getClassLoader().getResourceAsStream( Constants.File.SETTINGS );

		if (inputStream != null) {
			yamlMaps = yaml.load(inputStream);

			try {
				File myObj = new File(Constants.File.SETTINGS);
				if (myObj.createNewFile()) {
					System.out.println("File created: " + myObj.getName());
				} else {
					System.out.println("File already exists.");
				}
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}

		} else {
			throw new FileNotFoundException("Settings file '" + Constants.File.SETTINGS + "' not found in the classpath!");
		}
	}

}
