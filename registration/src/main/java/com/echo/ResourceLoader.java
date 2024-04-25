package com.echo;

import java.io.IOException;

public class ResourceLoader {
    // Get the absolute path of the file
    public String getResourcePath(String filename) throws IOException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(filename).getFile();
    }
}
