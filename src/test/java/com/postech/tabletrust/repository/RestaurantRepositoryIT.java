package com.postech.tabletrust.repository;

import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.utils.NewEntitiesHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext
@Transactional
public class RestaurantRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void shouldAllowCreateATable() {
        var totalRestaurants = restaurantRepository.count();
        assertThat(totalRestaurants).isNotNegative();
    }

    @Test
    void shouldAllowSaveRestaurant() {
        //Arrange
        Restaurant restaurant = NewEntitiesHelper.createARestaurant();

        //Act
        var restaurantFoud = restaurantRepository.save(restaurant);

        //Assert
        assertThat(restaurantFoud).isInstanceOf(Restaurant.class).isNotNull();
        assertThat(restaurantFoud.getId()).isNotNull(); // Verifica que um UUID foi gerado
        assertThat(restaurantFoud.getAddress()).isEqualTo(restaurant.getAddress());
        assertThat(restaurantFoud.getName()).isEqualTo(restaurant.getName());
    }

    @Test
    void shouldFindRestaurantsByNameAndAddressAndKitchenType() {
        // Arrange - cria e persiste um restaurante de teste
        Restaurant restaurant = NewEntitiesHelper.createARestaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setAddress("Test Address");
        restaurant.setKitchenType("Test Kitchen");

        restaurantRepository.save(restaurant);

        // Act - busca restaurantes usando o método do repositório
        List<Restaurant> foundRestaurants = restaurantRepository.findRestaurantsByNameAndAddressAndKitchenType(
                "Test Restaurant", "Test Address", "Test Kitchen");

        // Assert - verifica se o restaurante correto foi encontrado
        assertThat(foundRestaurants).hasSize(1);
        assertThat(foundRestaurants.get(0).getId()).isEqualTo(restaurant.getId());
    }

    private Restaurant createARestaurant(){
        LocalTime open = LocalTime.of(19, 0, 0);
        LocalTime close = LocalTime.of(23, 30, 0);

        return Restaurant.builder()
                //.id(UUID.randomUUID())
                .address("Fragonard")
                .kitchenType("Tapioca")
                .name("Restaurante-teste")
                .openingTime(open)
                .closingTime(close)
                .availableCapacity(100)
                .build();
    }
}
