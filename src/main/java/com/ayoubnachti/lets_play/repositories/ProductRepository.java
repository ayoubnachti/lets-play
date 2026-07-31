package com.ayoubnachti.lets_play.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ayoubnachti.lets_play.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}