package com.ayoubnachti.lets_play.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import com.ayoubnachti.lets_play.exceptions.custom.ResourceNotFoundException;

import jakarta.validation.Valid;

@RestController
class ThrowingTestController {

    @GetMapping("/test/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("Product", "test-id");
    }

    @GetMapping("/test/duplicate")
    public void duplicate() {
        throw new DuplicateKeyException("email already exists");
    }

    @GetMapping("/test/generic")
    public void generic() {
        throw new RuntimeException("boom");
    }

    @PostMapping("/test/validate")
    public void validate(@Valid @RequestBody TestPayload payload) {
    }

    static class TestPayload {
        @jakarta.validation.constraints.NotBlank
        public String name;
    }
}