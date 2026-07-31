package com.ayoubnachti.lets_play.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ayoubnachti.lets_play.dtos.ProductResponse;
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
}