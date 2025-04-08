package com.postech.tabletrust.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.tabletrust.dto.ReservationDTO;
import com.postech.tabletrust.entity.Reservation;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.GlobalExceptionHandler;
import com.postech.tabletrust.exception.NotFoundException;
import com.postech.tabletrust.gateways.CustomerGateway;
import com.postech.tabletrust.gateways.ReservationGateway;
import com.postech.tabletrust.gateways.RestaurantGateway;
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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.postech.tabletrust.utils.NewEntitiesHelper.reservationID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationGateway reservationGateway;
    @Mock
    private CustomerGateway customerGateway;
    @Mock
    private RestaurantGateway restaurantGateway;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {

        openMocks = MockitoAnnotations.openMocks(this);
        ReservationController ReservationController = new ReservationController(reservationGateway, customerGateway, restaurantGateway);
        mockMvc = MockMvcBuilders.standaloneSetup(ReservationController).setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
            response.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
        }, "/*").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    class NewReservation {
        @Test
        void testNewReservation_Success() throws Exception {
            doReturn(NewEntitiesHelper.createAEmptyReservationList())
                    .when(reservationGateway).findRestaurantReservationByDate(anyString(), anyString());
            doReturn(NewEntitiesHelper.createACustomer())
                    .when(customerGateway).findCustomer(anyString());
            doReturn(NewEntitiesHelper.createARestaurant())
                    .when(restaurantGateway).findRestaurantById(anyString());
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(null);
            mockMvc.perform(post("/reservations")
                    .contentType(MediaType.APPLICATION_JSON).content(asJsonString(reservationDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        void testNewReservation_NotFoundCustomer() throws Exception {
            doReturn(NewEntitiesHelper.createAEmptyReservationList())
                    .when(reservationGateway).findRestaurantReservationByDate(anyString(), anyString());
            doReturn(null)
                    .when(customerGateway).findCustomer(anyString());
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(null);
            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON).content(asJsonString(reservationDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testNewReservation_NotFoundRestaurant() throws Exception {
            doReturn(NewEntitiesHelper.createAEmptyReservationList())
                    .when(reservationGateway).findRestaurantReservationByDate(anyString(), anyString());
            doReturn(NewEntitiesHelper.createACustomer())
                    .when(customerGateway).findCustomer(anyString());
            doReturn(null)
                    .when(restaurantGateway).findRestaurantById(anyString());
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(null);
            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON).content(asJsonString(reservationDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testNewReservation_NoCapacity() throws Exception {
            Restaurant restaurant = NewEntitiesHelper.createARestaurant();
            doReturn(NewEntitiesHelper.createAFullReservationList(restaurant.getAvailableCapacity()))
                    .when(reservationGateway).findRestaurantReservationByDate(anyString(), anyString());
            doReturn(NewEntitiesHelper.createACustomer())
                    .when(customerGateway).findCustomer(anyString());
            doReturn(restaurant)
                    .when(restaurantGateway).findRestaurantById(anyString());
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(null);
            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON).content(asJsonString(reservationDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("O restaurante não tem mesas disponíveis");
                    });
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarReservation_DataNula() throws Exception {
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(null);
            reservationDTO.setReservationDate(null);
            mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON).content(asJsonString(reservationDTO))).andExpect(status().isBadRequest()).andExpect(result -> {
                String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                assertThat(json).contains("Validation error");
                assertThat(json).contains("A data não pode ser nula");
            });
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarReservation_QuantidadeNula() throws Exception {
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(null);
            reservationDTO.setQuantity(null);
            mockMvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON).content(asJsonString(reservationDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                assertThat(json).contains("Validation error");
                assertThat(json).contains("A quantidade de lugares não pode ser nula");
            });
        }

        /*-------------------------------------- UPDATE --------------------------------------*/
        @Test
        void shouldUpdateReservation() throws Exception {
            doReturn(NewEntitiesHelper.createAEmptyReservationList())
                    .when(reservationGateway).findRestaurantReservationByDate(anyString(), anyString());
            doReturn(NewEntitiesHelper.createAReservation())
                    .when(reservationGateway).findReservation(anyString());
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(reservationID.toString());

            mockMvc.perform(patch("/reservations/"+ reservationID.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(reservationDTO)))
                    .andExpect(status().isOk());
        }
        @Test
        void deveGerarExcecao_QuandoIdPathForDiferente_onUpdate() throws Exception {
            doReturn(NewEntitiesHelper.createAEmptyReservationList())
                    .when(reservationGateway).findRestaurantReservationByDate(anyString(), anyString());
            doReturn(NewEntitiesHelper.createAReservation())
                    .when(reservationGateway).findReservation(anyString());
            ReservationDTO reservationDTO = NewEntitiesHelper.gerarReservationInsertRequest(reservationID.toString());

            mockMvc.perform(patch("/reservations/"+ UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(reservationDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("ID da reserva incorreto");
            });
        }

    }

    @Nested
    class ListReservations {
        @Test
        void testFindReservations_Success() throws Exception {

            // Prepare test data
            Reservation reservation1 = NewEntitiesHelper.createAReservationRandom();
            Reservation reservation2 = NewEntitiesHelper.createAReservationRandom();
            List<ReservationDTO> reservations = Arrays.asList(reservation1, reservation2)
                    .stream()
                    .map(ReservationDTO::new)
                    .collect(Collectors.toList());;

            when(reservationGateway.listAllReservations()).thenReturn(reservations);

            mockMvc.perform(get("/reservations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(reservation1.getId().toString()))
                    .andExpect(jsonPath("$[0].customerId").value(reservation1.getCustomerId().toString()))
                    .andExpect(jsonPath("$[0].restaurantId").value(reservation1.getRestaurant().getId().toString()))
                    .andExpect(jsonPath("$[1].id").value(reservation2.getId().toString()))
                    .andExpect(jsonPath("$[1].customerId").value(reservation2.getCustomerId().toString()))
                    .andExpect(jsonPath("$[1].restaurantId").value(reservation2.getRestaurant().getId().toString()));

            verify(reservationGateway, times(1)).listAllReservations();
        }

        @Test
        void testFindReservationsByCustomer_Success() throws Exception {

            // Prepare test data
            Reservation reservation1 = NewEntitiesHelper.createAReservationRandom();
            List<ReservationDTO> reservations = Arrays.asList(reservation1)
                    .stream()
                    .map(ReservationDTO::new)
                    .collect(Collectors.toList());;

            when(reservationGateway.findCustomerReservation(anyString())).thenReturn(reservations);

            mockMvc.perform(get("/reservations?customerName={customerName}", reservation1.getCustomerId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(reservation1.getId().toString()))
                    .andExpect(jsonPath("$[0].customerId").value(reservation1.getCustomerId().toString()))
                    .andExpect(jsonPath("$[0].restaurantId").value(reservation1.getRestaurant().getId().toString()));

            verify(reservationGateway, times(1)).findCustomerReservation(reservation1.getCustomerId().toString());
        }

        @Test
        void testFindReservationsByRestaurant_Success() throws Exception {

            // Prepare test data
            Reservation reservation1 = NewEntitiesHelper.createAReservationRandom();
            List<ReservationDTO> reservations = Arrays.asList(reservation1)
                    .stream()
                    .map(ReservationDTO::new)
                    .collect(Collectors.toList());;

            when(reservationGateway.findRestaurantReservation(anyString())).thenReturn(reservations);

            mockMvc.perform(get("/reservations?restaurantId={restaurantId}", reservation1.getRestaurant().getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(reservation1.getId().toString()))
                    .andExpect(jsonPath("$[0].customerId").value(reservation1.getCustomerId().toString()))
                    .andExpect(jsonPath("$[0].restaurantId").value(reservation1.getRestaurant().getId().toString()));

            verify(reservationGateway, times(1)).findRestaurantReservation(reservation1.getRestaurant().getId().toString());
        }

        @Test
        void testFindReservationsByRestaurantAndCustomer_Success() throws Exception {

            // Prepare test data
            Reservation reservation1 = NewEntitiesHelper.createAReservationRandom();
            List<ReservationDTO> reservations = Arrays.asList(reservation1)
                    .stream()
                    .map(ReservationDTO::new)
                    .collect(Collectors.toList());;

            when(reservationGateway.findReservationsByRestaurantAndCustomer(anyString(), anyString())).thenReturn(reservations);

            mockMvc.perform(get("/reservations?restaurantId={restaurantId}&customerName={customerName}", reservation1.getRestaurant().getId(), reservation1.getCustomerId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(reservation1.getId().toString()))
                    .andExpect(jsonPath("$[0].customerId").value(reservation1.getCustomerId().toString()))
                    .andExpect(jsonPath("$[0].restaurantId").value(reservation1.getRestaurant().getId().toString()));

            verify(reservationGateway, times(1)).findReservationsByRestaurantAndCustomer(reservation1.getRestaurant().getId().toString(), reservation1.getCustomerId().toString());
        }

        @Test
        void testFindReservations_Error() throws Exception {

            when(reservationGateway.findRestaurantReservation(anyString())).thenThrow(NotFoundException.class);

            mockMvc.perform(get("/reservations?restaurantId={restaurantId}", UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound());

            verify(reservationGateway, times(1)).findRestaurantReservation(anyString());
        }


    }

    @Nested
    class DeleteReservations {
        @Test
        void testDeleteReservation_Success() throws Exception {

            mockMvc.perform(delete("/reservations/{id}", UUID.randomUUID().toString()))
                    .andExpect(status().isNoContent());
            verify(reservationGateway).deleteReservation(anyString());

        }

        @Test
        void testDeleteReservation_Error() throws Exception {

            doThrow(RuntimeException.class).when(reservationGateway).deleteReservation(anyString());

            mockMvc.perform(delete("/reservations/{id}", UUID.randomUUID().toString()))
                    .andExpect(status().isNotFound());

            verify(reservationGateway, times(1)).deleteReservation(anyString());
        }
    }

}
