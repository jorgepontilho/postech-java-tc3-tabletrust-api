package com.postech.tabletrust.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.tabletrust.dto.FeedBackCreateDTO;
import com.postech.tabletrust.entity.FeedBack;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.exception.GlobalExceptionHandler;
import com.postech.tabletrust.exception.InvalidReservationException;
import com.postech.tabletrust.gateways.FeedBackGateway;
import com.postech.tabletrust.gateways.ReservationGateway;
import com.postech.tabletrust.usecases.FeedBackUseCase;
import com.postech.tabletrust.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FeedBackControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FeedBackGateway feedBackGateway;

    @Mock
    private ReservationGateway reservationGateway;

    @Mock
    private FeedBackUseCase feedBackUseCase;
    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        FeedBackController feedBackController = new FeedBackController(reservationGateway, feedBackGateway, feedBackUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(feedBackController)
                .setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class createAFeedBack {

        @Test
        void shouldCreateAFeedBack() throws Exception {
            FeedBack feedBack = NewEntitiesHelper.createAFeedBack();
            FeedBackCreateDTO feedBackCreateDTO = feedBack.convertToDTO();
            when(feedBackUseCase.registerFeedBack(feedBackCreateDTO, reservationGateway)).thenReturn(feedBack);

            // Convertendo FeedBackCreateDTO para JSON
            String feedBackCreateDTOJson = new ObjectMapper().writeValueAsString(feedBackCreateDTO);

            mockMvc.perform(
                    post("/feedbacks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(feedBackCreateDTOJson))
                            .andExpect(status().isOk());

            verify(feedBackUseCase, times(1)).registerFeedBack(feedBackCreateDTO, reservationGateway);
        }

        @Test
        void shouldReturnExceptionIfReservationNotFound() throws Exception {
            FeedBack feedBack = NewEntitiesHelper.createAFeedBack();
            feedBack.setReservationId(UUID.randomUUID());

            FeedBackCreateDTO feedBackCreateDTO = feedBack.convertToDTO();

            when(feedBackUseCase.registerFeedBack(feedBackCreateDTO, reservationGateway)).thenThrow(InvalidReservationException.class);

            // Convertendo FeedBackCreateDTO para JSON
            String feedBackCreateDTOJson = new ObjectMapper().writeValueAsString(feedBackCreateDTO);

            mockMvc.perform(
                            post("/feedbacks")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(feedBackCreateDTOJson))
                    .andExpect(status().isConflict());
            verify(feedBackUseCase, times(1)).registerFeedBack(feedBackCreateDTO, reservationGateway);
        }

        @Test
        void shouldReturnExceptionIfReservationNotValid() throws Exception {
            //Assert
            FeedBack feedBack = NewEntitiesHelper.createAFeedBack();
            Reservation reservation = NewEntitiesHelper.createAReservation();
            reservation.setApproved(false);
            feedBack.setReservationId(reservation.getId());

            FeedBackCreateDTO feedBackCreateDTO = feedBack.convertToDTO();

            when(feedBackUseCase.registerFeedBack(feedBackCreateDTO, reservationGateway)).thenThrow(InvalidReservationException.class);

            String feedBackCreateDTOJson = new ObjectMapper().writeValueAsString(feedBackCreateDTO);

            mockMvc.perform(
                            post("/feedbacks")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(feedBackCreateDTOJson))
                    .andExpect(status().isConflict());
            verify(feedBackUseCase, times(1)).registerFeedBack(feedBackCreateDTO, reservationGateway);
        }
    }

    @Nested
    class listAFeedBack {

        void shouldListAFeedBack(){
            fail("shouldListAFeedBack nao implementado");
        }

        void shouldThrowExceptionIfRestaurantIdNotFound(){
            fail("shouldThrowExceptionIfRestaurantIdNotFound nao implementado");
        }

        void shouldFoundFeedBackById() {
            fail("souldFoundFeedBackById nao implementado");
        }
    }

    @Nested
    class deleteAFeedBack {
        void shouldDeleteAFeedback() {
            fail("deleteAFeedBack nao implementado"); // Somente o customer pode excluir seu comentario
        }
    }
}
