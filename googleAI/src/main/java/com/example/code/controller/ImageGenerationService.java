package com.example.code.controller;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import jakarta.annotation.Nullable;
import org.apache.http.HttpException;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Service responsible for generating images using Google's Gemini AI model.
 * This service interacts with the Gemini API to generate images based on text prompts
 * and optional reference images.
 */
@Service
public class ImageGenerationService {

    private final String imageGenerationModel = "gemini-2.5-flash-image";

    private final Client genaiClient;
    private final ImageStorageService imageStorageService;

    /**
     * Constructs an ImageGenerationService with the required dependencies.
     *
     * @param genaiClient Google GenAI client for API communication
     * @param imageStorageService Service for storing generated images
     */
    public ImageGenerationService(Client genaiClient, ImageStorageService imageStorageService) {
        this.genaiClient = genaiClient;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Generates images based on a text prompt and optional reference images.
     *
     * @param prompt Text description of the image to generate
     * @param images Optional reference images to guide the generation (can be null)
     * @return List of generated image names that can be used to retrieve the images
     */
    public List<String> generateImages(String prompt, @Nullable List<MultipartFile> images) {
        List<Part> parts = new ArrayList<>();
        parts.add(Part.fromText(prompt)); // Add prompt

        if (images != null) {
            List<Part> imageParts = images.stream()
                    .map(image -> {
                        try {
                            return Part.fromBytes(image.getBytes(), image.getContentType());
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            parts.addAll(imageParts); // Add images
        }

        Content content = Content.builder().parts(parts).build();
        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseModalities(List.of("Text", "Image"))
                .build();

        try {
            GenerateContentResponse response = this.genaiClient.models.generateContent(imageGenerationModel, content, config);
            List<Image> generatedImages = getImages(response);

            generatedImages.forEach(image -> this.imageStorageService.store(image.imageName(), image.imageBytes()));
            return generatedImages.stream().map(Image::imageName).toList();
        } catch (Exception e) {
            System.out.println("HTTP Error: " + e.getMessage());
        }

        return Collections.emptyList();
    }

    /**
     * Extracts image data from the Gemini API response.
     *
     * @param response The response from the Gemini API
     * @return List of Image objects containing the generated images
     */
    private List<Image> getImages(GenerateContentResponse response) {
        ImmutableList<Part> responseParts = response.parts();
        if (responseParts == null || responseParts.isEmpty()) {
            return Collections.emptyList();
        }
        return responseParts
                .stream()
                .map(Part::inlineData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(inlineData -> inlineData.data().isPresent())
                .map(inlineData -> {
                    MimeType mimeType = MimeType.valueOf(inlineData.mimeType().get()); // imageMimeType
                    return new Image(
                            "%s.%s".formatted(UUID.randomUUID().toString(), mimeType.getSubtype()),
                            inlineData.data().get(), // imageBytes
                            mimeType.toString());
                })
                .toList();
    }

    /**
     * Record class representing a generated image with its name, binary data, and MIME type.
     *
     * @param imageName The name of the image file
     * @param imageBytes The binary data of the image
     * @param mimeType The MIME type of the image
     */
    record Image(String imageName, byte[] imageBytes, String mimeType) {}
}
