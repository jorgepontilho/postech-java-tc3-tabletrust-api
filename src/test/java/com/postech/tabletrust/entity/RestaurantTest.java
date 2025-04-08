package com.postech.tabletrust.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestaurantTest {

    @Test
    public void testClone() {
        // Arrange
        Restaurant original = new Restaurant();
        original.setId(UUID.randomUUID());
        original.setName("Restaurante A");

        // Act
        Restaurant cloned = original.clone();

        // Assert
        assertEquals(original.getId(), cloned.getId());
        assertEquals(original.getName(), cloned.getName());
    }
}
