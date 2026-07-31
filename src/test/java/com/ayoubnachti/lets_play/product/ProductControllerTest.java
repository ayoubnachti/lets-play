package com.ayoubnachti.lets_play.product;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ayoubnachti.lets_play.config.SecurityConfig;
import com.ayoubnachti.lets_play.controllers.ProductController;
import com.ayoubnachti.lets_play.dtos.ProductResponse;
import com.ayoubnachti.lets_play.services.ProductService;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductService productService;

  @Test
  void getProductsReturnsProductList() throws Exception {
    ProductResponse product = new ProductResponse(
        "1", "banane", "mochti banane", 12., "user-1",
        Instant.parse("2026-07-01T10:00:00Z"), Instant.parse("2026-07-01T10:00:00Z"));

    when(productService.findAll()).thenReturn(List.of(product));

    mockMvc.perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("1"))
        .andExpect(jsonPath("$[0].name").value("banane"))
        .andExpect(jsonPath("$[0].price").value(12.));
  }

  @Test
  void postProductsWithoutAuthReturns403() throws Exception {
    mockMvc.perform(post("/products")
        .contentType("application/json")
        .content("{\"name\": \"Test\", \"price\": 10.0}"))
        .andDo(print())
        .andExpect(status().isForbidden());
  }
}