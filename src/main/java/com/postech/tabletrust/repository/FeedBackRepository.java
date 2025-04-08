package com.postech.tabletrust.repository;

import com.postech.tabletrust.entity.FeedBack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, UUID> {

    Page<FeedBack> findByRestaurantId(UUID restaurantId, Pageable pageable);

    List<FeedBack> findByRestaurantId(UUID restaurantId);
}
