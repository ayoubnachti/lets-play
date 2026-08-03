package com.ayoubnachti.lets_play.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ayoubnachti.lets_play.dtos.ProductRequest;
import com.ayoubnachti.lets_play.dtos.ProductResponse;
import com.ayoubnachti.lets_play.enums.Role;
import com.ayoubnachti.lets_play.exceptions.custom.ResourceNotFoundException;
import com.ayoubnachti.lets_play.models.Product;
import com.ayoubnachti.lets_play.repositories.ProductRepository;
import com.ayoubnachti.lets_play.security.AuthenticatedUser;

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

  public ProductResponse updateProduct(String id, ProductRequest request, AuthenticatedUser currentUser) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product", id));

    boolean isOwner = product.getUserId().equals(currentUser.id());
    boolean isAdmin = Role.valueOf(currentUser.role()) == Role.ADMIN;

    if (!isOwner && !isAdmin) {
      throw new AccessDeniedException("Not authorized to update this product");
    }

    product.setName(request.name());
    product.setDescription(request.description());
    product.setPrice(request.price());

    Product saved = productRepository.save(product);
    return ProductResponse.from(saved);
  }

  public void deleteProduct(String id, AuthenticatedUser currentUser) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product", id));

    boolean isOwner = product.getUserId().equals(currentUser.id());
    boolean isAdmin = Role.valueOf(currentUser.role()) == Role.ADMIN;

    if (!isOwner && !isAdmin) {
      throw new AccessDeniedException("Not authorized to delete this product");
    }

    productRepository.deleteById(id);
  }

  public ProductResponse findById(String id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    return ProductResponse.from(product);
  }
}