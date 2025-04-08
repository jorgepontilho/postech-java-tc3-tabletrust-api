package com.postech.tabletrust.gateway;

import com.postech.tabletrust.entity.FeedBack;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.gateways.FeedBackGateway;
import com.postech.tabletrust.repository.FeedBackRepository;
import com.postech.tabletrust.repository.RestaurantRepository;
import com.postech.tabletrust.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FeedBackGatewayTest {
    @Mock
    private FeedBackRepository feedBackRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    private FeedBackGateway feedBackGateway;

    AutoCloseable openMocks;

    @BeforeEach
    void setup(){
        openMocks = MockitoAnnotations.openMocks(this);
        feedBackGateway = new FeedBackGateway(feedBackRepository, restaurantRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class ListFeedbacks {
        @Test
        void shouldListAllFeedBacksByARestaurantPageable(){
            Restaurant restaurant = NewEntitiesHelper.createARestaurant();
            FeedBack fb = NewEntitiesHelper.createAFeedBack();
            Pageable pageable = PageRequest.of(0,10);
            List<FeedBack> fbList = Arrays.asList(fb);

            Page<FeedBack> fbPage = new PageImpl<>(fbList,pageable,fbList.size());

            // Configuração dos mocks para o repositório
            when(restaurantRepository.findById(restaurant.getId())).thenReturn(Optional.of(restaurant));
            when(feedBackRepository.findByRestaurantId(restaurant.getId(),pageable)).thenReturn(fbPage);

            //Assert
            var listOfFB = feedBackGateway.feedBackByRestaurantIdPageable(pageable, restaurant.getId());

            assertThat(listOfFB).contains(fb);
        }

        @Test //OK
        void shouldFindByID(){
            //Assert
            var fb = NewEntitiesHelper.createAFeedBack();
            UUID id = fb.getId();

            when(feedBackRepository.findById(id)).thenReturn(Optional.of(fb));

            // Act
            FeedBack fbFound = feedBackGateway.findById(id);

            //Assert
            assertThat(fbFound).isNotNull().isInstanceOf(FeedBack.class);
            assertThat(fbFound).isEqualTo(fb);
            verify(feedBackRepository, times(1)).findById(any(UUID.class));
        }
    }

    @Nested //OK
    class CreateFeedbacks{
        @Test //OK
        void shouldCreateANewFeedBack() throws Exception {
            FeedBack feedBack = NewEntitiesHelper.createAFeedBack();

          when(feedBackGateway.createFeedback(any(FeedBack.class))).thenAnswer( i -> i.getArgument(0)); // Mock o save

            //Act
            var feedback = feedBackGateway.createFeedback(feedBack);

            //Assert
            assertThat(feedback).isNotNull().isInstanceOf(FeedBack.class);
        }
    }

    @Nested
    class DeleteFeedbacks{
        @Test
        void shouldDeleteByID(){
            //Assert
            var feedback = NewEntitiesHelper.createAFeedBack();
            UUID id = feedback.getId();

            when(feedBackRepository.findById(id)).thenReturn(Optional.of(feedback));
            doNothing().when(feedBackRepository).deleteById(id);

            var fbRemoved = feedBackGateway.deleteById(id);
            assertThat(fbRemoved).isTrue();

            verify(feedBackRepository, times(1)).deleteById(any(UUID.class));
        }
    }
}
