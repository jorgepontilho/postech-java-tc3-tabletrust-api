package com.postech.tabletrust.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Builder
public record RestaurantDTO (

    UUID id,
    @NotNull
    String name,
    @NotNull
    String address,
    @NotNull
    String kitchenType,

    //@CreationTimestamp
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime openingTime,

    //@CreationTimestamp corrigir
    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime closingTime,

    @NotNull
    Integer availableCapacity
){

}


