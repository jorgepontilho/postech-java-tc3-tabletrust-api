package com.postech.tabletrust.repository;

import com.postech.tabletrust.entity.FeedBack;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FeedBackRepositoryTest {

    @Mock //Deja dans les dependances
    private FeedBackRepository feedBackRepository;

    AutoCloseable openMocks;

    @BeforeEach
    void setup(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void shouldListFeedbacksByRestaurantId(){
        //Arrange
        FeedBack feedback = NewEntitiesHelper.createAFeedBack();
        Restaurant restaurant = NewEntitiesHelper.createARestaurant();
        Reservation reservation = NewEntitiesHelper.createAReservation();
        feedback.setRestaurantId(restaurant.getId());
        feedback.setReservationId(reservation.getId());

        Pageable pageable = PageRequest.of(0, 10);
        List<FeedBack> feedBackList = Arrays.asList(feedback, feedback); // Adicione objetos Feedback reais aqui
        Page<FeedBack> feedBackPage = new PageImpl<>(feedBackList, pageable, feedBackList.size());

        when(feedBackRepository.findByRestaurantId(restaurant.getId(),pageable)).thenReturn(feedBackPage);

        //Act
        Page<FeedBack> result = feedBackRepository.findByRestaurantId(restaurant.getId(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(feedBackList.size(), result.getContent().size());
        assertTrue(result.getContent().containsAll(feedBackList));
    }

    @Test
    void shouldCreateAFeedBack(){
        var feedback = NewEntitiesHelper.createAFeedBack();

        when(feedBackRepository.save(feedback)).thenReturn(feedback);

        var fbFound = feedBackRepository.save(feedback);

        assertThat(fbFound).isNotNull().isInstanceOf(FeedBack.class);
        assertThat(fbFound).isEqualTo(feedback);
    }

    @Test
    void shouldFindById(){
        var feedback = NewEntitiesHelper.createAFeedBack();
        UUID id = feedback.getId();

        when(feedBackRepository.findById(id)).thenReturn(Optional.of(feedback));

        var fbFound = feedBackRepository.findById(id);

        assertThat(fbFound).isPresent().containsSame(feedback);
    }

    @Test
    void shouldDeleteById(){
        var feedback = NewEntitiesHelper.createAFeedBack();
        UUID id = feedback.getId();

        doNothing().when(feedBackRepository).deleteById(any(UUID.class));

        feedBackRepository.deleteById(id);

        verify(feedBackRepository, times(1)).deleteById(any(UUID.class));
    }
}
