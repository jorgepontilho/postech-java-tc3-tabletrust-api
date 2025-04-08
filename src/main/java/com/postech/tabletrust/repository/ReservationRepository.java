package com.postech.tabletrust.repository;

import com.postech.tabletrust.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findAllByCustomerId(UUID customerId);
    List<Reservation> findAllByRestaurantId(UUID restaurantId);
    Optional<List<Reservation>> findAllByReservationDateBetween(LocalDateTime start, LocalDateTime end);
}
