package com.postech.tabletrust.controller;

import com.postech.tabletrust.dto.RestaurantDTO;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.NotFoundException;
import com.postech.tabletrust.interfaces.IRestaurantGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final IRestaurantGateway restaurantGateway;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new restaurant", responses = {
            @ApiResponse(description = "The new restaurant was created", responseCode = "201")
    })
    public ResponseEntity newRestaurant(@Valid @RequestBody RestaurantDTO restaurantDTO) {
        log.info("create restaurant [{}]", restaurantDTO.name());
        try {
            Restaurant restaurantCreated = restaurantGateway.newRestaurant(restaurantDTO);
            return new ResponseEntity<>(restaurantCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("")
    @Operation(summary = "List of restaurants", responses = {
            @ApiResponse(description = "All restaurants by kitchen type", responseCode = "200")
    })
    public ResponseEntity<?> findRestaurantsByNameAndAddressAndKitchenType(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String kitchenType
    ) {
        log.info("GetMapping - findRestaurantsByNameAndAddressAndKitchenType");
        List<Restaurant> restaurants = restaurantGateway.findRestaurantsByNameAndAddressAndKitchenType(name, address, kitchenType);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Once restaurant", responses = {
            @ApiResponse(description = "Get the restaurant by ID", responseCode = "200")
    })
    public ResponseEntity<?> findRestaurant(@PathVariable String id) {
        log.info("GetMapping - findRestaurant ");
        try {
            UUID.fromString(id);
            //UUID uuid = UUID.fromString(id);
            Restaurant restaurant = restaurantGateway.findRestaurantById(id);
            return new ResponseEntity<>(restaurant, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update all informations of a restaurant", responses = {
            @ApiResponse(description = "The restaurant was updated", responseCode = "200")
    })
    public ResponseEntity<?> updateRestaurant(
            @PathVariable String id,
            @RequestBody @Valid Restaurant restaurant) {
        log.info("PutMapping - updateRestaurant");
        try {
            UUID uuid = UUID.fromString(id);
            Restaurant newRestaurant = restaurantGateway.updateRestaurant(uuid, restaurant);
            return new ResponseEntity<>(newRestaurant, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a restaurant by ID", responses = {
            @ApiResponse(description = "The restaurant was deleted", responseCode = "200")
    })
    public ResponseEntity<?> deleteRestaurant(@PathVariable String id) {
        log.info("DeleteMapping - deleteRestaurant");
        try {
            var uuid = UUID.fromString(id);
            restaurantGateway.deleteRestaurant(uuid);
            return new ResponseEntity<>("Restaurante removido", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
