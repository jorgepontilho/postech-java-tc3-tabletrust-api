package com.postech.tabletrust.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.postech.tabletrust.dto.RestaurantDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_restaurant")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Restaurant implements Cloneable{
    @Id
    @GenericGenerator(name = "UUID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotEmpty(message = "[name] não pode estar vazio")
    private String name;

    @NotEmpty(message = "[address] não pode estar vazio")
    private String address;

    @NotEmpty(message = "[kitchenType] não pode estar vazio")
    private String kitchenType; //TODO enum ou tabela de domínio

    //@CreationTimestamp
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime openingTime;

    //@CreationTimestamp corrigir
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime closingTime;

    @NotNull(message = "[availableCapacity] não pode ser nula")
    private Integer availableCapacity;

    public Restaurant(UUID uuid) {
        this.id = uuid;
    }

    public Restaurant(RestaurantDTO restaurantDTO){
        this.id = restaurantDTO.id();
        this.name = restaurantDTO.name();
        this.address = restaurantDTO.address();
        this.kitchenType = restaurantDTO.kitchenType();
        this.openingTime = restaurantDTO.openingTime();
        this.closingTime = restaurantDTO.closingTime();
        this.availableCapacity = restaurantDTO.availableCapacity();
    }

    @Override
    public Restaurant clone() {
        try {
            return (Restaurant) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
