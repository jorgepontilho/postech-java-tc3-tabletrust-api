package com.postech.tabletrust.repository;

import com.postech.tabletrust.entity.Restaurant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

public class RestaurantRepositoryTest {

    @Mock //Deja dans les dependances
    private RestaurantRepository restaurantRepository;

    AutoCloseable openMocks;

    @BeforeEach // Before chaque test le setup doit etre habilité et after each le tear down doit etre executé aussi
    void setup(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void shouldAllowToCreateARestaurant(){
        //Arrange
        var restaurant = createARestaurant();
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);  //Deixa um pouco mais genérico

        // Act
        var restaurantSave = restaurantRepository.save(restaurant);

        // Assert
        assertThat(restaurantSave)
                .isNotNull()
                .isEqualTo(restaurant);
        //boa pratica
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void shouldAllowSearchARestaurant(){
        //Arrange
        var restaurant = createARestaurant();
        UUID id = restaurant.getId();

        when(restaurantRepository.findById(id))
                .thenReturn(Optional.of(restaurant));
        // Act
        var restaurantFind = restaurantRepository.findById(id);

        // Assert
        assertThat(restaurantFind)
                .isPresent()
                .containsSame(restaurant);
    }

    @Test
    void shouldAllowSearchARestaurantByNameAddressAndKitchen() {
        //Arrange
        var restaurant = createARestaurant();
        UUID id = restaurant.getId();
        List<Restaurant> restaurants = List.of(restaurant);

        when(restaurantRepository.findRestaurantsByNameAndAddressAndKitchenType(
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getKitchenType()))
                .thenReturn(restaurants);
        // Act
        var restaurantFind = restaurantRepository.findRestaurantsByNameAndAddressAndKitchenType(
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getKitchenType()
                );

        // Assert
        assertThat(restaurantFind).contains(restaurant);
        assertThat(restaurantFind).hasSize(1);
        assertThat(restaurantFind.get(0).getId()).isEqualTo(restaurant.getId());
    }

    @Test
    void shouldAllowUpdateRestaurant(){
        //Arrange
        Restaurant restaurant = createARestaurant();
        when(restaurantRepository.save(any(Restaurant.class))).then(returnsFirstArg());  //Deixa um pouco mais genérico

        // Act
        restaurant.setAddress("UPDATED ADDRESS");
        Restaurant restaurantUpdated = restaurantRepository.save(restaurant);

        // Assert
        assertThat(restaurantUpdated).isNotNull();
        assertThat(restaurantUpdated.getAddress()).isEqualTo("UPDATED ADDRESS");

        //boa pratica
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    void shouldAllowDeleteRestaurant(){
        //Arrange
        Restaurant restaurant = createARestaurant();
        UUID id = restaurant.getId();

        doNothing().when(restaurantRepository).deleteById(any(UUID.class));
        //Act
        restaurantRepository.deleteById(id);

        //Assert
        verify(restaurantRepository, times(1)).deleteById(any(UUID.class));
    }

    private Restaurant createARestaurant(){
        LocalTime open = LocalTime.of(19, 0, 0);
        LocalTime close = LocalTime.of(23, 30, 0);

        return Restaurant.builder()
                .id(UUID.randomUUID())
                .address("Fragonard")
                .kitchenType("Tapioca")
                .name("Restaurante-teste")
                .openingTime(open)
                .closingTime(close)
                .availableCapacity(100)
                .build();
    }
}
