package com.greazi.discordbotfoundation.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.greazi.discordbotfoundation.constants.Constants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSettings {

	private String token;
	private String activity;


	SimpleSettings() {

	}

	public String getToken() {
		return token;
	}

	public String getActivity() {
		return activity;
	}

}
