package com.postech.tabletrust.usecases;

import com.postech.tabletrust.entity.Customer;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.ReservationNotAvailable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationUseCase {

    public static Reservation validateInsertReservation(List<Reservation> reservationList, Restaurant restaurant, Customer customer, Integer quantity, String date) {
        validateMaxCapacity(reservationList, restaurant);
        return new Reservation(restaurant, customer, quantity, date);
    }

    public static Reservation validateUpdateReservation(String strId,
                                                        @NotNull(message = "A reserva não pode ser nula.")Reservation oldReservation,
                                                        @NotNull(message = "A data não pode ser nula.") String newDate,
                                                        @NotNull(message = "A quantidade de lugares não pode ser nula.") Integer quantity,
                                                        Boolean isApproved, List<Reservation> reservationList) {

        if (!oldReservation.getId().toString().equals(strId)) {
            throw new IllegalArgumentException("ID da reserva incorreto");
        }
        if(!reservationList.isEmpty()){
            validateMaxCapacity(reservationList, reservationList.get(0).getRestaurant());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(newDate, formatter);
        oldReservation.setQuantity(quantity);
        oldReservation.setReservationDate(dateTime);
        oldReservation.setApproved(isApproved);
        return oldReservation;
    }

    private static void validateMaxCapacity(List<Reservation> reservationList, Restaurant restaurant) {
        if (!(restaurant.getAvailableCapacity() > reservationList.size())){
            throw new ReservationNotAvailable("O restaurante não tem mesas disponíveis");
        }
    }

}
