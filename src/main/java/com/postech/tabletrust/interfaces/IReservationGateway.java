package com.postech.tabletrust.interfaces;

import com.postech.tabletrust.dto.CustomerDTO;
import com.postech.tabletrust.dto.ReservationDTO;
import com.postech.tabletrust.entity.Reservation;

import java.util.List;

public interface IReservationGateway {

    Reservation createReservation(Reservation reservation);

    public ReservationDTO updateReservation(ReservationDTO reservation);

    public void deleteReservation(String strId);

    public List<ReservationDTO> findRestaurantReservation(String restaurantId);

    public List<ReservationDTO> findCustomerReservation(String customerId);

    public Reservation findReservation(String strId);

    public List<ReservationDTO> listAllReservations();

    List<Reservation> findRestaurantReservationByDate(String restaurantId, String date);

    List<ReservationDTO> findReservationsByRestaurantAndCustomer(String restaurantId, String customerName);
}
