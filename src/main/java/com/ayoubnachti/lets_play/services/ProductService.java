package com.ayoubnachti.lets_play.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ayoubnachti.lets_play.dtos.ProductRequest;
import com.ayoubnachti.lets_play.dtos.ProductResponse;
import com.ayoubnachti.lets_play.models.Product;
import com.ayoubnachti.lets_play.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public List<ProductResponse> findAll() {
    return productRepository.findAll()
        .stream()
        .map(ProductResponse::from)
        .toList();
  }

  public ProductResponse createProduct(ProductRequest request, String ownerId) {
    Product product = Product.builder()
        .name(request.name())
        .description(request.description())
        .price(request.price())
        .userId(ownerId)
        .build();

    Product saved = productRepository.save(product);
    return ProductResponse.from(saved);
  }
}