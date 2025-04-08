package com.postech.tabletrust.usecases;

import com.postech.tabletrust.dto.CustomerDTO;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.ReservationNotAvailable;
import com.postech.tabletrust.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CreateReservationUseCase {

    public Reservation validateInsertReservation(Reservation reservation, List<Reservation> reservationList, CustomerDTO customerDTO, ReservationRepository reservationRepository){
        try {
            Restaurant restaurant = reservationList.stream().findFirst().get().getRestaurant();
            if (restaurant.getAvailableCapacity() > reservationList.size()){
                validarReserva(reservation, customerDTO);

            } else {
                throw new ReservationNotAvailable("O restaurante não tem mesas disponíveis");
            }

        } catch (Exception e) {
            log.error("error creating a reservation to customer [{}], restaurant [{}]", reservation.getCustomerId(), reservation.getRestaurant().getId());
        }
        return reservation;
    }

    private static void validarReserva(Reservation reservation, CustomerDTO customerDTO) {

        if (customerDTO == null) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }
        /* TODO
        if (restaurantDTO == null) {
            throw new IllegalArgumentException("Restaurante não encontrado");
        } */
        if (reservation == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula");
        }
    }

}
