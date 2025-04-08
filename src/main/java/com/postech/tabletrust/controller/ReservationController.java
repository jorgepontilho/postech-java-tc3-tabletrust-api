package com.postech.tabletrust.controller;

import com.postech.tabletrust.dto.ReservationDTO;
import com.postech.tabletrust.entity.Customer;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.gateways.CustomerGateway;
import com.postech.tabletrust.gateways.ReservationGateway;
import com.postech.tabletrust.gateways.RestaurantGateway;
import com.postech.tabletrust.usecases.ReservationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationGateway reservationGateway;
    private final CustomerGateway customerGateway;
    private final RestaurantGateway restaurantGateway;

    public ReservationController(ReservationGateway reservationGateway, CustomerGateway customerGateway, RestaurantGateway restaurantGateway) {
        this.reservationGateway = reservationGateway;
        this.customerGateway = customerGateway;
        this.restaurantGateway = restaurantGateway;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new reservation", responses = {
            @ApiResponse(description = "The new reservation was created", responseCode = "201")
    })
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        log.info("create reservation for customer [{}]", reservationDTO.getCustomerId());

        try {
            List<Reservation> reservationList = reservationGateway.findRestaurantReservationByDate(reservationDTO.getRestaurantId(), reservationDTO.getReservationDate());
            Customer customer = customerGateway.findCustomer(reservationDTO.getCustomerId());
            Restaurant restaurant = restaurantGateway.findRestaurantById(reservationDTO.getRestaurantId());
            Reservation reservation = ReservationUseCase.validateInsertReservation(reservationList, restaurant, customer,
                    reservationDTO.getQuantity(), reservationDTO.getReservationDate());

            Reservation reservationCreated = reservationGateway.createReservation(reservation);

            return new ResponseEntity<>(reservationCreated, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Request for update a reservation", responses = {
            @ApiResponse(description = "The reservation was updated", responseCode = "200")
    })
    public ResponseEntity<?> updateReservation(@PathVariable String id, @RequestBody @Valid ReservationDTO reservationDTO) {
        log.info("update reservation [{}]", reservationDTO.getId());
        try {
            Reservation reservationOld = reservationGateway.findReservation(id);
            customerGateway.findCustomer(reservationDTO.getCustomerId());
            List<Reservation> reservationList = reservationGateway.findRestaurantReservationByDate(reservationDTO.getRestaurantId(), reservationDTO.getReservationDate());

            ReservationUseCase.validateUpdateReservation(id, reservationOld, reservationDTO.getReservationDate(),
                    reservationDTO.getQuantity(), reservationDTO.getApproved(), reservationList);
            ReservationDTO newReservation = reservationGateway.updateReservation(reservationDTO);
            return new ResponseEntity<>(newReservation, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation by ID", responses = {
            @ApiResponse(description = "The reservation was deleted", responseCode = "200")
    })
    public ResponseEntity<?> deleteReservation(@PathVariable String id) {
        log.info("delete Reservation [{}]", id);
        try {
            reservationGateway.deleteReservation(id);
            return new ResponseEntity<>("reserva removida", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("")
    @Operation(summary = "Find reservations by parameters", responses = {
            @ApiResponse(description = "All reservations by parameters", responseCode = "200")
    })
    public ResponseEntity<?> findRestaurantReservation( @RequestParam(required = false) String restaurantId,
                                                        @RequestParam(required = false) String customerId) {
        log.info("find all reservations by parameters restaurant id: {} - customer id: {}",
                restaurantId == null ? "empty" : restaurantId, customerId == null ? "empty" : customerId);
        try {
            if (StringUtils.hasText(restaurantId) && StringUtils.hasText(customerId)) {
                List<ReservationDTO> reservations = reservationGateway.findReservationsByRestaurantAndCustomer(restaurantId, customerId);
                return ResponseEntity.ok(reservations);
            } else if (StringUtils.hasText(restaurantId)) {
                List<ReservationDTO> reservations = reservationGateway.findRestaurantReservation(restaurantId);
                return ResponseEntity.ok(reservations);
            } else if (StringUtils.hasText(customerId)) {
                List<ReservationDTO> reservations = reservationGateway.findCustomerReservation(customerId);
                return ResponseEntity.ok(reservations);
            } else {
                List<ReservationDTO> reservations = reservationGateway.listAllReservations();
                return ResponseEntity.ok(reservations);
            }
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

