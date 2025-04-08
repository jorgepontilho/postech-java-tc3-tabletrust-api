package com.postech.tabletrust.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.tabletrust.dto.CustomerDTO;
import com.postech.tabletrust.dto.ReservationDTO;
import com.postech.tabletrust.dto.RestaurantDTO;
import com.postech.tabletrust.entity.Customer;
import com.postech.tabletrust.entity.FeedBack;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.entity.Restaurant;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewEntitiesHelper {

    private static UUID customerID = UUID.fromString("7247101a-9bab-414a-83e5-a61b07e2146a");
    private static UUID restaurantID = UUID.fromString("c68b4872-6073-4dff-8199-a24c74d4c763");
    public static UUID reservationID = UUID.fromString("38f6df39-9118-4610-a435-7572648540a0");
    private static UUID feedbackID = UUID.fromString("7cad184d-6b00-4e20-bdeb-d4e224cf3bbd");

    /* ----------------------------- Feedback ------------------------------- */
    //La meme reservation id
    public static FeedBack createAFeedBack(){

        return FeedBack.builder()
                .id(feedbackID)
                .comment("OTIMO")
                .restaurantId(restaurantID)
                .customerId(customerID)
                .reservationId(reservationID)
                .stars(5)
                .build();
    }

    /* ----------------------------- Restaurant ----------------------------- */
    public static Restaurant createARestaurant(){
        LocalTime open = LocalTime.of(19, 0, 0);
        LocalTime close = LocalTime.of(23, 30, 0);

        return Restaurant.builder()
                .id(restaurantID)
                .address("Fragonard")
                .kitchenType("Sopa")
                .name("Restaurante-teste")
                .openingTime(open)
                .closingTime(close)
                .availableCapacity(100)
                .build();
    }

    public static RestaurantDTO gerarRestaurantInsertRequest(){
        LocalTime open = LocalTime.of(19, 0, 0);
        LocalTime close = LocalTime.of(23, 30, 0);

        return RestaurantDTO.builder()
                .id(restaurantID)
                .address("Fragonard")
                .kitchenType("Sopa")
//                .name("Restaurante-teste")
                .openingTime(open)
                .closingTime(close)
                .availableCapacity(100)
                .build();
    }

    /* ----------------------------- Customer ------------------------------- */
    public static Customer createACustomer(){
        Customer customer = new Customer(customerID, "joe");
        return customer;
    }
    public static CustomerDTO gerarCustomerInsertRequest() {
        return CustomerDTO.builder()
                .id(UUID.randomUUID().toString())
                .nome("John")
                .build();
    }
    public static List<CustomerDTO> gerarCustomerListRequest() {
        List<CustomerDTO>  customerDTOList = new ArrayList<>();
        customerDTOList.add( CustomerDTO.builder()
                .id(UUID.randomUUID().toString())
                .nome("John")
                .build());

        customerDTOList.add( CustomerDTO.builder()
                .id(UUID.randomUUID().toString())
                .nome("Ana")
                .build());

        return customerDTOList;
    }
 /* ----------------------------- Reservation ---------------------------- */
     public static ReservationDTO gerarReservationInsertRequest( String uuid) {
         return ReservationDTO.builder()
                 .id(uuid == null ? UUID.randomUUID().toString() : reservationID.toString())
                 .restaurantId("2b9c1a1e-c257-4bc6-8efe-c1db33d4c52c")
                 .customerId("cecad256-a3c3-4c09-833c-36586cd00f45")
                 .reservationDate("2024-02-20 20:30:00")
                 .quantity(4)
                 .build();
     }

    public static Reservation createAReservation() {
        LocalDateTime in3HoursAgo = LocalDateTime.now().minusHours(3);

        return Reservation.builder()
                .id(reservationID)
                .reservationDate(in3HoursAgo)
                .restaurant(new Restaurant(restaurantID))
                .customerId(customerID)
                .quantity(4)
                .approved(true)
                .build();
    }

    public static Reservation createAReservationRandom() {
        LocalDateTime in3HoursAgo = LocalDateTime.now().minusHours(3);

        return Reservation.builder()
                .id(UUID.randomUUID())
                .reservationDate(in3HoursAgo)
                .restaurant(new Restaurant(restaurantID))
                .customerId(UUID.randomUUID())
                .quantity(4)
                .approved(true)
                .build();
    }

    public static List<Reservation> createAEmptyReservationList() {
         List<Reservation> reservationList = new ArrayList<>();
         return reservationList;
    }

    public static List<Reservation> createAFullReservationList(Integer capacity) {
        List<Reservation> reservationList = new ArrayList<>();
        for (int i = 0; i < capacity; i++){
            reservationList.add(createAReservationRandom());
        }
        return reservationList;
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
