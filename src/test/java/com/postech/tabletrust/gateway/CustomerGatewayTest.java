package com.postech.tabletrust.gateway;

import com.postech.tabletrust.dto.CustomerDTO;
import com.postech.tabletrust.entity.Customer;
import com.postech.tabletrust.gateways.CustomerGateway;
import com.postech.tabletrust.repository.CustomerRepository;
import com.postech.tabletrust.utils.NewEntitiesHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerGatewayTest {

    CustomerGateway customerGateway;
    @Mock
    private CustomerRepository customerRepository;
    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        customerGateway = new CustomerGateway (customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Nested
    class InsertCustomer {
        @Test
        void devePermitirRegistrarCliente() {
            Customer customer = NewEntitiesHelper.createACustomer();
            when(customerGateway.createCustomer(any(Customer.class))).thenAnswer(i -> i.getArgument(0));
            Customer customerNew = customerGateway.createCustomer(customer);
            assertThat(customerNew).isNotNull().isInstanceOf(Customer.class);
        }
    }

    @Nested
    class UpdateCustomer {
        @Test
        void devePermitirAtualizarCliente() {
            Customer customer = NewEntitiesHelper.createACustomer();
            when(customerGateway.updateCustomer(any(Customer.class))).thenAnswer(i -> i.getArgument(0));
            Customer customerNew = customerGateway.updateCustomer(customer);
            assertThat(customerNew).isNotNull().isInstanceOf(Customer.class);
        }
    }

    @Nested
    class DeleteCustomer {
        @Test
        void devePermitirApagarCliente() {
            Customer customer = NewEntitiesHelper.createACustomer();

            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
            doNothing().when(customerRepository).deleteById(customer.getId());

            boolean fbRemoved = customerGateway.deleteCustomer(customer.getId().toString());
            assertThat(fbRemoved).isTrue();

            verify(customerRepository, times(1)).deleteById(any(UUID.class));

        }
        @Test
        void deveFalharAoApagarClienteComIdInvalido() {
            String idNotValid = "123";
            boolean fbRemoved = customerGateway.deleteCustomer(idNotValid);
            assertThat(fbRemoved).isFalse();
        }
    }

    @Nested
    class FindCustomer {
        @Test
        void devePermitirPesquisarUmCliente() {
            Customer customer = NewEntitiesHelper.createACustomer();
            when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

            Customer customerFound = customerGateway.findCustomer(customer.getId().toString());

            assertThat(customerFound).isNotNull().isInstanceOf(Customer.class);
            assertThat(customerFound).isEqualTo(customer);
            verify(customerRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveFalharPesquisarUmClienteComIdInvalido() {
            String idNotValid = "123";
            Customer customerFound = customerGateway.findCustomer(idNotValid);
            assertThat(customerFound).isNull();
        }

        @Test
        void devePermitirListarTodosClientes() {
            List<CustomerDTO> customerDTOList = NewEntitiesHelper.gerarCustomerListRequest();
            List<Customer> customerList = new Customer().toList(customerDTOList);

            when(customerRepository.findAll()).thenReturn(customerList);

            List<Customer> customerFoundList = customerGateway.listAllCustomers();

            assertThat(customerFoundList).isNotNull().isInstanceOf(List.class);
            assertThat(customerFoundList).isEqualTo(customerList);
            verify(customerRepository, times(1)).findAll();
        }
    }
}