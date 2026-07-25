package com.ayoubnachti.lets_play.product;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ayoubnachti.lets_play.models.Product;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductValidationTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void setUp() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    factory.close();
  }

  private Product.ProductBuilder validProduct() {
    return Product.builder()
        .name("Banan")
        .description("mochti banan")
        .price(13.)
        .userId("user123");
  }

  @Test
  void validProduct_hasNoViolations() {
    Product product = validProduct().build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations).isEmpty();
  }

  @Test
  void blankName_isRejected() {
    Product product = validProduct().name("").build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations)
        .hasSize(1)
        .extracting(v -> v.getPropertyPath().toString())
        .containsExactly("name");
  }

  @Test
  void nullName_isRejected() {
    Product product = validProduct().name(null).build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .containsExactly("name");
  }

  @Test
  void nameOverMaxLength_isRejected() {
    Product product = validProduct().name("a".repeat(101)).build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .containsExactly("name");
  }

  @Test
  void nullDescription_isAllowed() {
    Product product = validProduct().description(null).build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations).isEmpty();
  }

  @Test
  void descriptionOverMaxLength_isRejected() {
    Product product = validProduct().description("a".repeat(1001)).build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .containsExactly("description");
  }

  @Test
  void nullPrice_isRejected() {
    Product product = validProduct().price(null).build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .containsExactly("price");
  }

  @Test
  void negativePrice_isRejected() {
    Product product = validProduct().price(-1.).build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .containsExactly("price");
  }

  @Test
  void zeroPrice_isAllowed() {
    Product product = validProduct().price(0.).build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations).isEmpty();
  }

  @Test
  void blankUserId_isRejected() {
    Product product = validProduct().userId("").build();

    Set<ConstraintViolation<Product>> violations = validator.validate(product);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .containsExactly("userId");
  }
}