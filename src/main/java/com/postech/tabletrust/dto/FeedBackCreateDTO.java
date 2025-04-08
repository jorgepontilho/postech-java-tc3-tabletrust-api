package com.postech.tabletrust.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FeedBackCreateDTO(
        @NotNull
        UUID restaurantId,
        @NotNull
        UUID customerId,
        @NotNull
        UUID reservationId,
        String comment,
        @NotNull
        int stars
) {
}
