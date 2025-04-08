package com.postech.tabletrust.controller;

import com.postech.tabletrust.dto.RestaurantDTO;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.NotFoundException;
import com.postech.tabletrust.interfaces.IRestaurantGateway;
import com.postech.tabletrust.utils.NewEntitiesHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RestaurantControllerTest {

    private RestaurantController restaurantController;

    @Mock
    private IRestaurantGateway restaurantGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        restaurantController = new RestaurantController(restaurantGateway);
    }

    @Nested
    class CreateRestaurant {
        @Test
        void testNewRestaurant_Success() {
            // Crie um objeto RestaurantDTO simulado
            RestaurantDTO restaurantDTO = NewEntitiesHelper.gerarRestaurantInsertRequest();

            // Crie um objeto Restaurant simulado
            Restaurant restaurantCreated = new Restaurant();
            restaurantCreated.setId(UUID.randomUUID());

            // Configure o comportamento do mock
            when(restaurantGateway.newRestaurant(any(RestaurantDTO.class))).thenReturn(restaurantCreated);

            // Chame o método a ser testado
            ResponseEntity<?> response = restaurantController.newRestaurant(restaurantDTO);

            // Verifique se o status da resposta é CREATED
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            // Verifique se o restaurante retornado é o esperado
            assertEquals(restaurantCreated, response.getBody());
        }

        @Test
        void testNewRestaurant_ValidationException() {
            // Mocking the dependencies
            RestaurantDTO restaurantDTO = NewEntitiesHelper.gerarRestaurantInsertRequest();

            // Mocking the behavior of restaurantGateway.newRestaurant() to throw an exception
            when(restaurantGateway.newRestaurant(any(RestaurantDTO.class))).thenThrow(new RuntimeException("Some error message"));

            // Creating an instance of the controller
            RestaurantController restaurantController = new RestaurantController(restaurantGateway);

            // Invoking the method under test
            ResponseEntity responseEntity = restaurantController.newRestaurant(restaurantDTO);

            // Assertions
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertEquals("Some error message", responseEntity.getBody());
        }
    }

    @Nested
    class ReadRestaurant {
        @Test
        void testFindRestaurantsByNameAndAddressAndKitchenType_Success() {
            String name = "Pizza Place";
            String address = "123 Main St";
            String kitchenType = "Italian";

            List<Restaurant> expectedRestaurants = new ArrayList<>();
            // Adicione restaurantes simulados à lista conforme necessário

            when(restaurantGateway.findRestaurantsByNameAndAddressAndKitchenType(name, address, kitchenType))
                    .thenReturn(expectedRestaurants);

            ResponseEntity<?> response = restaurantController.findRestaurantsByNameAndAddressAndKitchenType(name, address, kitchenType);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedRestaurants, response.getBody());
        }

        @Test
        void testFindRestaurant_Success() {
            UUID restaurantId = UUID.randomUUID();
            Restaurant expectedRestaurant = new Restaurant();
            when(restaurantGateway.findRestaurantById(restaurantId.toString())).thenReturn(expectedRestaurant);

            ResponseEntity<?> response = restaurantController.findRestaurant(restaurantId.toString());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedRestaurant, response.getBody());
        }

        @Test
        void testFindRestaurant_NotRegisteredId_ReturnNotFoundException() {
            // Arrange
            UUID notFoundUuid = UUID.randomUUID();
            when(restaurantGateway.findRestaurantById(notFoundUuid.toString())).thenThrow(NotFoundException.class);

            // Act
            ResponseEntity<?> response = restaurantController.findRestaurant(notFoundUuid.toString());

            // Assertions
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(restaurantGateway, times(1)).findRestaurantById(notFoundUuid.toString());
        }

        @Test
        void testFindRestaurant_InvalidId() {
            ResponseEntity<?> response = restaurantController.findRestaurant("invalid-id");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("ID inválido", response.getBody());
        }
    }

    @Nested
    class UpdeteRestaurant {
        @Test
        void testUpdateRestaurant_Success () {
            UUID existingRestaurantId = UUID.randomUUID();
            Restaurant existingRestaurant = new Restaurant();
            existingRestaurant.setId(existingRestaurantId);

            Restaurant updatedRestaurant = new Restaurant();
            // Preencha os campos do restaurante atualizado conforme necessário

            when(restaurantGateway.updateRestaurant(existingRestaurantId, updatedRestaurant)).thenReturn(updatedRestaurant);

            ResponseEntity<?> response = restaurantController.updateRestaurant(existingRestaurantId.toString(), updatedRestaurant);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(updatedRestaurant, response.getBody());
        }

        @Test
        void testUpdateRestaurant_InvalidInputIdAndRestaurant_ReturnBadRequest() {
            // Act
            ResponseEntity<?> response = restaurantController.updateRestaurant("invalid-uuid", new Restaurant());

            // Assertions
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("ID inválido", response.getBody());
            verify(restaurantGateway, never()).updateRestaurant(any(), any());
        }

        @Test
        void testUpdateRestaurant_NotRegisteredId_ReturnNotFoundException() {
            // Arrange
            UUID notFoundUuid = UUID.randomUUID();
            when(restaurantGateway.updateRestaurant(notFoundUuid, new Restaurant())).thenThrow(RuntimeException.class);

            // Act
            ResponseEntity<?> response = restaurantController.updateRestaurant(notFoundUuid.toString(), new Restaurant());

            // Assertions
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(restaurantGateway, times(1)).updateRestaurant(notFoundUuid, new Restaurant());
        }
    }

    @Nested
    class DeleteRestaurant {
        @Test
        void testDeleteRestaurant_Success() {
            // Arrange
            UUID validUuid = UUID.randomUUID();

            // Act
            ResponseEntity<?> response = restaurantController.deleteRestaurant(validUuid.toString());

            // Assertions
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Restaurante removido", response.getBody());
            verify(restaurantGateway, times(1)).deleteRestaurant(validUuid);
        }

        @Test
        void testDeleteRestaurant_InvalidInputId_ReturnBadRequest() {
            // Act
            ResponseEntity<?> response = restaurantController.deleteRestaurant("invalid-uuid");

            // Assertions
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("ID inválido", response.getBody());
            verify(restaurantGateway, never()).deleteRestaurant(any());
        }

        @Test
        void testDeleteRestaurant_NotRegisteredId_ReturnNotFoundException() {
            // Arrange
            UUID notFoundUuid = UUID.randomUUID();
            doThrow(RuntimeException.class).when(restaurantGateway).deleteRestaurant(notFoundUuid);

            // Act
            ResponseEntity<?> response = restaurantController.deleteRestaurant(notFoundUuid.toString());

            // Assertions
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            verify(restaurantGateway, times(1)).deleteRestaurant(notFoundUuid);
        }
    }

}
