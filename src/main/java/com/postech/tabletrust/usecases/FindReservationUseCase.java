package com.postech.tabletrust.usecases;

import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class FindReservationUseCase {

    public List<Reservation> findByDate(LocalDateTime dateTime, ReservationRepository reservationRepository){
        try {
                LocalDateTime start = dateTime.toLocalDate().atTime(LocalTime.MIN);
                LocalDateTime end = dateTime.toLocalDate().atTime(LocalTime.MAX);

            List<Reservation> reservationList = reservationRepository.findAllByReservationDateBetween(start, end).orElse(Collections.emptyList());
            return  reservationList;
        } catch (Exception e) {
            log.error("error searching a reservation by date [{}]", dateTime.toString());
            throw e;
        }
    }

}
