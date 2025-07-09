package com.example.FashionStoreBE.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Converter
public class ImageListConverter implements AttributeConverter<List<String>, String> {
    private static final Logger logger = LoggerFactory.getLogger(ImageListConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> images) {
        if (images == null || images.isEmpty()) {
            logger.debug("Converting empty or null image list to JSON: []");
            return "[]";
        }
        try {
            String json = objectMapper.writeValueAsString(images);
            logger.debug("Converted image list to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            logger.error("Error converting image list to JSON: {}", e.getMessage());
            throw new RuntimeException("Không thể chuyển đổi danh sách hình ảnh sang JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String json) {
        if (json == null || json.trim().isEmpty()) {
            logger.debug("JSON string is null or empty, returning empty list");
            return new ArrayList<>();
        }
        try {
            List<String> images = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            logger.debug("Converted JSON to image list: {}", images);
            return images;
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON to image list: JSON={}, Error={}", json, e.getMessage());
            throw new RuntimeException("Không thể chuyển đổi JSON sang danh sách hình ảnh: " + json, e);
        }
    }
}