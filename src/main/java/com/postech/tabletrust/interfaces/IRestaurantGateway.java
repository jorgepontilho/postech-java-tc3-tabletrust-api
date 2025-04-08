package com.postech.tabletrust.interfaces;

import com.postech.tabletrust.dto.RestaurantDTO;
import com.postech.tabletrust.entity.Restaurant;

import java.util.List;
import java.util.UUID;

public interface IRestaurantGateway {

    Restaurant newRestaurant(RestaurantDTO restaurantDTO);

    Restaurant findRestaurantById(String id);

    List<Restaurant> findRestaurantsByNameAndAddressAndKitchenType(String name, String address, String kitchenType);

    Restaurant updateRestaurant(UUID id, Restaurant newRestaurant);

    boolean deleteRestaurant(UUID id);
}
