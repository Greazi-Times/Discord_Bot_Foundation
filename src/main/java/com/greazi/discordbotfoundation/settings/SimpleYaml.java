package com.greazi.discordbotfoundation.settings;

import com.greazi.discordbotfoundation.constants.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * SimpleJson is a tool that checks if the settings file is there,
 * As well as making some easy methods to generate a proper settings file.
 */
public class SimpleYaml {

	Yaml yaml = new Yaml();
	InputStream inputStream = this.getClass()
			.getClassLoader()
			.getResourceAsStream(Constants.File.SETTINGS);
}
