package com.greazi.discordbotfoundation.settings;

import com.google.common.annotations.Beta;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Beta
public class TempStorage {

    public static String folder = "temp";

    public static String getFolder() {
        return folder;
    }

    public static void saveFile(File file) {
        try {
            FileUtils.copyFile(file, new File(folder + "/" + file.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        try {
            FileUtils.forceDelete(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveText(String text, String fileName) {
        try{
            FileUtils.writeStringToFile(new File(folder + "/" + fileName), text, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static @Nullable String readText(String fileName) {
        try{
            return FileUtils.readFileToString(new File(folder + "/" + fileName), StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
