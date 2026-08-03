package com.ayoubnachti.lets_play.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.ayoubnachti.lets_play.dtos.ProductRequest;
import com.ayoubnachti.lets_play.dtos.ProductResponse;
import com.ayoubnachti.lets_play.exceptions.custom.ResourceNotFoundException;
import com.ayoubnachti.lets_play.models.Product;
import com.ayoubnachti.lets_play.repositories.ProductRepository;
import com.ayoubnachti.lets_play.security.AuthenticatedUser;
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

    @Test
    void updateProduct_updatesAndReturnsProduct_whenUserIsOwner() {
        Product existing = Product.builder()
                .id("1")
                .name("Old Name")
                .description("Old description")
                .price(10.)
                .userId("user-1")
                .createdAt(Instant.parse("2026-07-01T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-01T10:00:00Z"))
                .build();

        ProductRequest request = new ProductRequest("New Name", "New description", 20.);
        AuthenticatedUser currentUser = new AuthenticatedUser("user-1", "owner@test.com", "USER");

        when(productRepository.findById("1")).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse result = productService.updateProduct("1", request, currentUser);

        assertThat(result.name()).isEqualTo("New Name");
        assertThat(result.description()).isEqualTo("New description");
        assertThat(result.price()).isEqualTo(20.);
    }

    @Test
    void updateProduct_updatesAndReturnsProduct_whenUserIsAdmin() {
        Product existing = Product.builder()
                .id("1")
                .name("Old Name")
                .description("Old description")
                .price(10.)
                .userId("user-1")
                .createdAt(Instant.parse("2026-07-01T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-01T10:00:00Z"))
                .build();

        ProductRequest request = new ProductRequest("New Name", "New description", 20.);
        AuthenticatedUser currentUser = new AuthenticatedUser("admin-1", "admin@test.com", "ADMIN");

        when(productRepository.findById("1")).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse result = productService.updateProduct("1", request, currentUser);

        assertThat(result.name()).isEqualTo("New Name");
    }

    @Test
    void updateProduct_throwsAccessDenied_whenUserIsNeitherOwnerNorAdmin() {
        Product existing = Product.builder()
                .id("1")
                .name("Old Name")
                .description("Old description")
                .price(10.)
                .userId("user-1")
                .createdAt(Instant.parse("2026-07-01T10:00:00Z"))
                .updatedAt(Instant.parse("2026-07-01T10:00:00Z"))
                .build();

        ProductRequest request = new ProductRequest("New Name", "New description", 20.);
        AuthenticatedUser currentUser = new AuthenticatedUser("user-2", "other@test.com", "USER");

        when(productRepository.findById("1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> productService.updateProduct("1", request, currentUser))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateProduct_throwsResourceNotFound_whenProductDoesNotExist() {
        ProductRequest request = new ProductRequest("New Name", "New description", 20.);
        AuthenticatedUser currentUser = new AuthenticatedUser("user-1", "owner@test.com", "USER");

        when(productRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct("1", request, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteProduct_deletesProduct_whenOwner() {
        Product product = Product.builder().id("p1").userId("u1").build();
        AuthenticatedUser owner = new AuthenticatedUser("u1","owner@test.com", "USER");
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        productService.deleteProduct("p1", owner);

        verify(productRepository).deleteById("p1");
    }

    @Test
    void deleteProduct_deletesProduct_whenAdmin() {
        Product product = Product.builder().id("p1").userId("u1").build();
        AuthenticatedUser admin = new AuthenticatedUser("u2","admin@test.com", "ADMIN");
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        productService.deleteProduct("p1", admin);

        verify(productRepository).deleteById("p1");
    }

    @Test
    void deleteProduct_throwsNotFound_whenProductMissing() {
        when(productRepository.findById("p1")).thenReturn(Optional.empty());
        AuthenticatedUser user = new AuthenticatedUser("u1","owner@test.com", "USER");

        assertThatThrownBy(() -> productService.deleteProduct("p1", user))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void deleteProduct_throwsAccessDenied_whenNotOwnerNorAdmin() {
        Product product = Product.builder().id("p1").userId("u1").build();
        AuthenticatedUser other = new AuthenticatedUser("u2","other@test.com", "USER");
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.deleteProduct("p1", other))
                .isInstanceOf(AccessDeniedException.class);

        verify(productRepository, never()).deleteById(any());
    }
}
