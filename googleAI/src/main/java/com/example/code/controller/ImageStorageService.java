package com.example.code.controller;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service responsible for storing and retrieving images from the file system.
 * Images are stored in the "image-storage/" directory.
 */
@Service
public class ImageStorageService {

    private final String folder = "image-storage/";

    /**
     * Stores an image with the given name and byte content.
     *
     * @param imageName The name of the image file
     * @param bytes The binary data of the image
     */
    public void store(String imageName, byte[] bytes) {
        Path path = Paths.get(folder + imageName);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves an image by name.
     *
     * @param imageName The name of the image file to retrieve
     * @return The binary data of the image, or null if the image cannot be found
     */
    public byte[] get(String imageName) {
        Path path = Paths.get(folder + imageName);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
            return null;
        }
    }
}
