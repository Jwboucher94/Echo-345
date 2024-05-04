package com.echo;

import java.io.IOException;

public class ResourceLoader {
    // Get the absolute path of the file
    public String getResourcePath(String filename) throws IOException{
        String file;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {file = classLoader.getResource(filename).getFile();
        } catch (NullPointerException e) {
            throw new IOException("File not found");
        }
        return file;
    }
}
