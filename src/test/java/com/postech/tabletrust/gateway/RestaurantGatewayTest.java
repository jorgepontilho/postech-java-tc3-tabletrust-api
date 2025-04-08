import com.postech.tabletrust.dto.RestaurantDTO;
import com.postech.tabletrust.entity.Restaurant;
import com.postech.tabletrust.exception.NotFoundException;
import com.postech.tabletrust.gateways.RestaurantGateway;
import com.postech.tabletrust.repository.RestaurantRepository;
import com.postech.tabletrust.utils.NewEntitiesHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantGatewayTest {

    private RestaurantGateway restaurantGateway;

    @Mock
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        restaurantGateway = new RestaurantGateway(restaurantRepository);
    }

    @Test
    void testNewRestaurant_Success() {
        // Crie um objeto RestaurantDTO simulado
        RestaurantDTO restaurantDTO = NewEntitiesHelper.gerarRestaurantInsertRequest();

        // Crie um objeto Restaurant simulado
        Restaurant restaurantCreated = new Restaurant();
        restaurantCreated.setId(UUID.randomUUID());

        // Configure o comportamento do mock
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurantCreated);

        // Chame o método a ser testado
        Restaurant result = restaurantGateway.newRestaurant(restaurantDTO);

        // Verifique se o restaurante retornado tem um ID não nulo
        assertNotNull(result.getId());
        // Verifique se o restaurante retornado é o esperado
        assertEquals(restaurantCreated, result);
    }


    @Test
    void testFindRestaurantById_Success() {
        UUID restaurantId = UUID.randomUUID();
        Restaurant expectedRestaurant = new Restaurant();
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(expectedRestaurant));

        Restaurant result = restaurantGateway.findRestaurantById(restaurantId.toString());

        assertEquals(expectedRestaurant, result);
    }

    @Test
    void testFindRestaurantById_NotFound() {
        UUID invalidId = UUID.randomUUID();
        when(restaurantRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> restaurantGateway.findRestaurantById(invalidId.toString()));
    }

    @Test
    void testFindRestaurantsByNameAndAddressAndKitchenType_Success() {
        String name = "Pizza Place";
        String address = "123 Main St";
        String kitchenType = "Italian";

        List<Restaurant> expectedRestaurants = new ArrayList<>();
        // Adicione restaurantes simulados à lista conforme necessário

        when(restaurantRepository.findRestaurantsByNameAndAddressAndKitchenType(name, address, kitchenType))
                .thenReturn(expectedRestaurants);

        List<Restaurant> result = restaurantGateway.findRestaurantsByNameAndAddressAndKitchenType(name, address, kitchenType);

        assertEquals(expectedRestaurants, result);
    }

    @Test
    void testUpdateRestaurant_Success() {
        UUID existingRestaurantId = UUID.randomUUID();
        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setId(existingRestaurantId);

        Restaurant updatedRestaurant = new Restaurant();
        // Preencha os campos do restaurante atualizado conforme necessário

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        Restaurant result = restaurantGateway.updateRestaurant(existingRestaurantId, updatedRestaurant);

        assertEquals(updatedRestaurant, result);
    }

    @Test
    void testDeleteRestaurant_Success() {
        UUID restaurantId = UUID.randomUUID();

        boolean success = restaurantGateway.deleteRestaurant(restaurantId);

        assertTrue(success);
    }
}
