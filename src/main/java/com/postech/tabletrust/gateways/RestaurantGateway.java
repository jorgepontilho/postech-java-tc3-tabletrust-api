package com.postech.tabletrust.gateways;

import com.postech.tabletrust.dto.RestaurantDTO;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.NotFoundException;
import com.postech.tabletrust.interfaces.IRestaurantGateway;
import com.postech.tabletrust.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantGateway implements
        IRestaurantGateway {
    private final RestaurantRepository restaurantRepository;

    public RestaurantGateway(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Restaurant newRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = new Restaurant(restaurantDTO);
        restaurant.setId(UUID.randomUUID());
        return this.restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant findRestaurantById(String strId) {
        UUID uuid = UUID.fromString(strId);
        return restaurantRepository.findById(uuid).orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
    }

    @Override
    public List<Restaurant> findRestaurantsByNameAndAddressAndKitchenType(String name, String address, String kitchenType) {
        return restaurantRepository.findRestaurantsByNameAndAddressAndKitchenType(name, address, kitchenType);
    }

    @Override
    public Restaurant updateRestaurant(UUID id, Restaurant newRestaurant) { //TODO refacto avec DTO pour validation
        newRestaurant.setId(id);
        return restaurantRepository.save(newRestaurant);
    }

    @Override
    public boolean deleteRestaurant(UUID id) {
        restaurantRepository.deleteById(id);
        return true;
    }
}
