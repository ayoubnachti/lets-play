package com.ayoubnachti.lets_play.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayoubnachti.lets_play.dtos.ProductResponse;
import com.ayoubnachti.lets_play.models.Product;
import com.ayoubnachti.lets_play.repositories.ProductRepository;
import com.ayoubnachti.lets_play.services.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAll_returnsMappedProducts_whenProductsExist() {
        Product product = Product.builder()
                .id("1")
                .name("Banane")
                .description("Mooooooooooochti banan")
                .price(12.)
                .userId("user-1")
                .createdAt(Instant.parse("2026-07-01T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-01T10:00:00Z"))
                .build();

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo("1");
        assertThat(result.get(0).name()).isEqualTo("Banane");
        assertThat(result.get(0).price()).isEqualTo(12);
    }

    @Test
    void findAll_returnsEmptyList_whenNoProductsExist() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> result = productService.findAll();

        assertThat(result).isEmpty();
    }
}
