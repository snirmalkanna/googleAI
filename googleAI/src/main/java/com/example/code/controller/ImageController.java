package com.example.code.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for handling image generation and retrieval requests.
 * Provides endpoints for generating, viewing, and downloading images.
 */
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageGenerationService imageGenerationService;
    private final ImageStorageService imageStorageService;

    /**
     * Constructs an ImageController with the required services.
     *
     * @param imageGenerationService Service for generating images using AI
     * @param imageStorageService Service for storing and retrieving images
     */
    @Autowired
    public ImageController(ImageGenerationService imageGenerationService, ImageStorageService imageStorageService) {
        this.imageGenerationService = imageGenerationService;
        this.imageStorageService = imageStorageService;
    }

    /**
     * Generates images based on a text prompt and optional reference images.
     *
     * @param prompt Text description of the image to generate
     * @param images Optional reference images to guide the generation
     * @return List of generated image names that can be used to retrieve the images
     */
    @PostMapping
    public List<String> generate(@RequestParam("prompt") String prompt,
                                 @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        return this.imageGenerationService.generateImages(prompt, images);
    }

    /**
     * Retrieves an image by name and returns it for display in a browser.
     *
     * @param imageName Name of the image to retrieve
     * @return ResponseEntity containing the image data with appropriate content type
     */
    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> image(@PathVariable String imageName) {
        byte[] bytes = this.imageStorageService.get(imageName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
    }

    /**
     * Retrieves an image by name and returns it as a downloadable file.
     *
     * @param imageName Name of the image to download
     * @return ResponseEntity containing the image data with appropriate headers for download
     */
    @GetMapping("/{imageName}/download")
    public ResponseEntity<byte[]> download(@PathVariable String imageName) {
        byte[] bytes = this.imageStorageService.get(imageName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageName + "\"")
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

}
