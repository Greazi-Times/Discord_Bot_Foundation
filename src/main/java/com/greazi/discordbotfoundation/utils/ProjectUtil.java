package com.greazi.discordbotfoundation.utils;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ProjectUtils is a class that allows you to handle some basic stuff on the project
 * Not yet done and tested
 */
public class ProjectUtil {

    /**
     * Retrieve files
     * @return File names
     */
    public static String[] getFiles() {
        ArrayList<String> names = new ArrayList<>();
        try {
            CodeSource src = ProjectUtil.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                while(true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null) break;
                    names.add(e.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names.toArray(new String[0]);
    }

    /**
     * Retrieve classes according to their names
     * @param prefix
     * @return The class
     */
    public static Class<?>[] getClasses(String prefix) {
        return Arrays.stream(getFiles())
                .filter(fileName -> fileName.endsWith(".class"))
                .map(className -> className.replace("/", ".").replace(".class", ""))
                .filter(fileName -> fileName.startsWith(prefix))
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Class[]::new);
    }

    /**
     * Remove letters on the front
     * @param s String
     * @param am Amount
     * @return The edited message
     */
    public static String removeFront(String s, int am) {
        return s.substring(am);
    }

    /**
     * Remove letters on the end
     * @param s String
     * @param am Amount
     * @return The edited message
     */
    public static String removeEnd(String s, int am) {
        return s.substring(0, s.length() - am);
    }

    /**
     * Remove letters on both sides
     * @param s String
     * @param am Amount
     * @return The edited message
     */
    public static String removeBoth(String s, int am) {
        return s.substring(am, s.length() - am);
    }



}