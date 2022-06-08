package com.greazi.discordbotfoundation.utils;

import java.io.*;

public class ResourceCopier {

    public File saveResource(String name) throws IOException {
        return saveResource(name, true);
    }

    public File saveResource(String name, boolean replace) throws IOException {
        return saveResource(new File("."), name, replace);
    }

    public File saveResource(File outputDirectory, String name) throws IOException {
        return saveResource(outputDirectory, name, true);
    }

    public File saveResource(File outputDirectory, String name, boolean replace) throws IOException {
        if(!name.startsWith("/")) {
            name = "/" + name;
        }

        File out = new File(outputDirectory, name);
        if (!replace && out.exists())
            return out;

        // Step 1:
        InputStream resource = this.getClass().getResourceAsStream(name);
        if (resource == null)
            throw new FileNotFoundException(name + " (resource not found)");
        // Step 2 and automatic step 4
        try(InputStream in = resource;
            OutputStream writer = new BufferedOutputStream(
                    new FileOutputStream(out))) {
            // Step 3
            byte[] buffer = new byte[1024 * 4];
            int length;
            while((length = in.read(buffer)) >= 0) {
                writer.write(buffer, 0, length);
            }
        }
        return out;
    }

}
