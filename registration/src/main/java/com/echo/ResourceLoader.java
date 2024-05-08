package com.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceLoader {
    public String getResourceAsString(String filename) throws IOException {
        InputStream inputStream = getResourceAsStream(filename);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private InputStream getResourceAsStream(String filename) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename); 

        if (inputStream == null) {
            throw new IOException("File not found");
        }

        return inputStream;
    }
}
