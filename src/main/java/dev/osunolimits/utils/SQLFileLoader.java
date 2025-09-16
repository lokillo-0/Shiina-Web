package dev.osunolimits.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class SQLFileLoader {

    private final String directory;
    private final ClassLoader classLoader; 

    public SQLFileLoader(String directory, ClassLoader classLoader) {
        this.directory = directory.endsWith("/") ? directory : directory + "/";
        this.classLoader = classLoader;
    }

    /**
     * Loads the content of all .sql files from the specified directory.
     *
     * @return List of file contents
     * @throws IOException        If an I/O error occurs
     * @throws URISyntaxException If the resource URI is invalid
     */
    public List<String> loadSQLFiles() throws IOException, URISyntaxException {
        Enumeration<URL> resources = classLoader.getResources(directory);
        if (!resources.hasMoreElements()) {
            throw new IllegalStateException("Directory not found: " + directory);
        }

        while (resources.hasMoreElements()) {
            URL resourceURL = resources.nextElement();

            if (resourceURL.getProtocol().equals("jar")) {
                return getFilesFromJar(resourceURL).stream()
                        .map(this::readResource)
                        .collect(Collectors.toList());
            } else {
                return getFilesFromFileSystem(resourceURL).stream()
                        .map(this::readResource)
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    private List<String> getFilesFromJar(URL jarUrl) {
        try {
            // Extract JAR path from URL (e.g., jar:file:/path/to/jar!/directory/)
            String jarPath = jarUrl.getPath().substring(5, jarUrl.getPath().indexOf("!"));
            try (JarFile jarFile = new JarFile(jarPath)) {
                return jarFile.stream()
                        .filter(entry -> entry.getName().startsWith(directory) && entry.getName().endsWith(".sql") && !entry.isDirectory())
                        .map(entry -> entry.getName())
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load files from JAR: " + jarUrl, e);
        }
    }

    private List<String> getFilesFromFileSystem(URL directoryUrl) {
        try {
            return Files.list(Paths.get(directoryUrl.toURI()))
                    .filter(path -> path.toString().endsWith(".sql"))
                    .map(path -> directory + path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to load files from file system: " + directoryUrl, e);
        }
    }

    private String readResource(String resourcePath) {
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourcePath, e);
        }
    }
}