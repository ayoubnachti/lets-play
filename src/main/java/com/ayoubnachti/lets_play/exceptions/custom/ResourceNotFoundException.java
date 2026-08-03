package com.ayoubnachti.lets_play.exceptions.custom;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceType, String id) {
        super(resourceType + " not found: " + id);
    }
}