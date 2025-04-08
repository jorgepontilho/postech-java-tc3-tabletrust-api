package com.postech.tabletrust.gateways;

import com.postech.tabletrust.entity.FeedBack;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.NotFoundException;
import com.postech.tabletrust.repository.FeedBackRepository;
import com.postech.tabletrust.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class FeedBackGateway {
    private final FeedBackRepository feedbackRepository;
    private final RestaurantRepository restaurantRepository;

    public FeedBackGateway(FeedBackRepository feedbackRepository, RestaurantRepository restaurantRepository) {
        this.feedbackRepository = feedbackRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public FeedBack createFeedback(FeedBack feedback) {
        return this.feedbackRepository.save(feedback);
    }

    public FeedBack findById(UUID id) {
        return this.feedbackRepository.findById(id).orElseThrow(()-> new NotFoundException("ID não encontrado"));
    }

    public Boolean deleteById(UUID id) {
        this.feedbackRepository.deleteById(id);
        return true;
    }

    public Page<FeedBack> feedBackByRestaurantIdPageable(Pageable pageable, UUID restaurantId) {
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new IllegalArgumentException("Restaurant not found for ID " + restaurantId));
        return this.feedbackRepository.findByRestaurantId(restaurantId, pageable);

    }

    public List<FeedBack> listFeedBackByRestaurantId(UUID restaurantId) {
        Restaurant restaurant = this.restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new IllegalArgumentException("Restaurant not found for ID " + restaurantId));
        return this.feedbackRepository.findByRestaurantId(restaurantId);

    }
}