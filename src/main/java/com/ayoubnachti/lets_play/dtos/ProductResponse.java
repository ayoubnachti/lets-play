package com.ayoubnachti.lets_play.dtos;

import java.time.Instant;

import com.ayoubnachti.lets_play.models.Product;

public record ProductResponse(
        String id,
        String name,
        String description,
        Double price,
        String userId,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getUserId(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}