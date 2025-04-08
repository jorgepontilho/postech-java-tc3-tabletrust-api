package com.postech.tabletrust.repository;

import com.postech.tabletrust.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    @Query("SELECT r FROM Restaurant r WHERE " +
            "(:name        IS NULL OR r.name LIKE %:name%) AND " +
            "(:address     IS NULL OR r.address LIKE %:address%) AND " +
            "(:kitchenType IS NULL OR r.kitchenType LIKE %:kitchenType%)")
    List<Restaurant> findRestaurantsByNameAndAddressAndKitchenType(
            @Param("name") String name,
            @Param("address") String address,
            @Param("kitchenType") String kitchenType
    );
}
