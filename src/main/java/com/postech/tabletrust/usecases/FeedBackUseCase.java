package com.postech.tabletrust.usecases;

import com.postech.tabletrust.dto.FeedBackCreateDTO;
import com.postech.tabletrust.entity.FeedBack;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.exception.InvalidReservationException;
import com.postech.tabletrust.gateways.FeedBackGateway;
import com.postech.tabletrust.gateways.ReservationGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedBackUseCase {

    private final ReservationGateway reservationGateway;

    public FeedBack registerFeedBack(FeedBackCreateDTO feedBackCreateDTO, ReservationGateway reservationGateway) {
        reservationIsAvalaibleForNewFeedBack(feedBackCreateDTO.reservationId(), reservationGateway);
        FeedBack $fb = new FeedBack(feedBackCreateDTO);
        return $fb;
    }
    
    public static void reservationIsAvalaibleForNewFeedBack(UUID reservationId, ReservationGateway reservationGateway) {
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        //A regra criada é de que o customer so pode dar seu feedback 3 horas depois de iniciada a reserva
        // E se o status da reserva é approved = true senao quer dizer que ela foi anulada e o feedback nao pode ser feito

        Optional<Reservation> reservation = reservationGateway.findReservationBYUUID(reservationId);


        if (! reservation.isPresent()) {
            throw new InvalidReservationException("A reserva informada nao foi encontrada");
        }

        Reservation reservationFound = reservation.get();

        if ( !reservationFound.getApproved()){
            throw new InvalidReservationException("A reserva informada nao foi aprovada pelo restaurante");
        }

        if ( !reservationFound.getReservationDate().isBefore(threeHoursAgo)){
            throw new InvalidReservationException("Um comentario so pode ser criado 3 horas depois do inicio da reserva");
        }
    }

    public double numberOfStarsByRestaurant(UUID restaurantId, FeedBackGateway feedbackGateway) {
        List<FeedBack> listFb = feedbackGateway.listFeedBackByRestaurantId(restaurantId);

        OptionalDouble average = listFb.stream()
                .mapToInt(FeedBack::getStars)
                .average();

        return average.orElse(0.0);
    }
}
