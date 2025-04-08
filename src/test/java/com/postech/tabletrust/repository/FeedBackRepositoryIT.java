package com.postech.tabletrust.repository;

import com.postech.tabletrust.entity.FeedBack;
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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext
@ActiveProfiles("test")
@Transactional
public class FeedBackRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Test
    void shouldAllowCreateATable() {
        var totalFeedbacks = feedBackRepository.count();
        assertThat(totalFeedbacks).isNotNegative();
    }

    @Test
    void shouldCreateAFeedBack(){
        FeedBack feedBack = NewEntitiesHelper.createAFeedBack();

        var fbFound = feedBackRepository.save(feedBack);

        assertThat(fbFound).isInstanceOf(FeedBack.class).isNotNull();
        assertThat(fbFound.getId()).isNotNull();
    }
}
