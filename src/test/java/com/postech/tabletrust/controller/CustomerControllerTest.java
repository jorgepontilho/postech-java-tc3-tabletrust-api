package com.postech.tabletrust.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.tabletrust.dto.CustomerDTO;
import com.postech.tabletrust.entity.Customer;
import com.postech.tabletrust.exception.GlobalExceptionHandler;
import com.postech.tabletrust.gateways.CustomerGateway;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerControllerTest {
    private MockMvc mockMvc;
    @Mock
    private CustomerGateway customerGateway;
    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {

        openMocks = MockitoAnnotations.openMocks(this);
        CustomerController customerController = new CustomerController(customerGateway);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).setControllerAdvice(new GlobalExceptionHandler()).addFilter((request, response, chain) -> {
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
    class InsertCustomer {
        @Test
        void devePermitirRegistrarCliente() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            mockMvc.perform(post("/customers")
                            .contentType(MediaType.APPLICATION_JSON).content(asJsonString(customerDTO)))
                    .andExpect(status().isCreated());
        }

        @Test
        void deveGerarExcecaoQuandoRegistrarClienteNomeNulo() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            customerDTO.setNome(null);
            mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(asJsonString(customerDTO))).andExpect(status().isBadRequest()).andExpect(result -> {
                String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                assertThat(json).contains("O nome não pode ser nulo.");
            });
        }

        @Test
        void deveGerarExcecaoQuandoRegistrarClienteJaExistente() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            Customer customer = new Customer(customerDTO);

            when(customerGateway.findCustomer(customerDTO.getId())).thenReturn(customer);
            mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(asJsonString(customerDTO))).andExpect(status().isBadRequest()).andExpect(result -> {
                String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                assertThat(json).contains("Cliente já existe");
            });
        }
    }

    @Nested
    class UpdateCustomer {
        @Test
        void devePermitirAtualizarCliente() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            Customer customer = new Customer(customerDTO);

            when(customerGateway.findCustomer(customerDTO.getId())).thenReturn(customer);
            when(customerGateway.updateCustomer(customer)).thenReturn(customer);

            mockMvc.perform(put("/customers/{id}", customer.getId())
                            .contentType(MediaType.APPLICATION_JSON).content(asJsonString(customerDTO)))
                    .andExpect(status().isOk());
        }

        @Test
        void deveGerarExcecaoQuandoAtualizarClienteNomeNulo() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            Customer customer = new Customer(customerDTO);

            when(customerGateway.findCustomer(customerDTO.getId())).thenReturn(customer);
            when(customerGateway.updateCustomer(customer)).thenReturn(customer);

            customerDTO.setNome(null);
            mockMvc.perform(put("/customers/{id}", customer.getId())
                    .contentType(MediaType.APPLICATION_JSON).content(asJsonString(customerDTO))).andExpect(status().isBadRequest()).andExpect(result -> {
                String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                assertThat(json).contains("O nome não pode ser nulo.");
            });
        }
        @Test
        void deveGerarExcecaoQuandoAtualizarClienteNãoEncontrado() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            Customer customer = new Customer(customerDTO);

            when(customerGateway.updateCustomer(customer)).thenReturn(customer);

            mockMvc.perform(put("/customers/{id}", customer.getId())
                    .contentType(MediaType.APPLICATION_JSON).content(asJsonString(customerDTO))).andExpect(status().isBadRequest()).andExpect(result -> {
                String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                assertThat(json).contains("Cliente não encontrado.");
            });
        }
    }

    @Nested
    class DeleteCustomer {
        @Test
        void devePermitirApagarCliente() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            Customer customer = new Customer(customerDTO);

            when(customerGateway.findCustomer(customerDTO.getId())).thenReturn(customer);

            mockMvc.perform(delete("/customers/{id}", customer.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        void deveGerarExcecaoQuandoDeletarClienteNãoEncontrado() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            Customer customer = new Customer(customerDTO);

            mockMvc.perform(delete("/customers/{id}", customer.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Cliente não encontrado.");
                    });
        }
    }


    @Nested
    class FindCustomer {
        @Test
        void devePermitirPesquisarUmCliente() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();
            Customer customer = new Customer(customerDTO);

            when(customerGateway.findCustomer(customerDTO.getId())).thenReturn(customer);

            mockMvc.perform(get("/customers/{id}", customer.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains(customerDTO.getId());
                    });
        }

        @Test
        void devePermitirListarTodosClientes() throws Exception {
            List<CustomerDTO> customerDTOList = NewEntitiesHelper.gerarCustomerListRequest();
            List<Customer> customerList = new Customer().toList(customerDTOList);

            when(customerGateway.listAllCustomers()).thenReturn(customerList);

            mockMvc.perform(get("/customers/")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains(customerDTOList.get(0).getId());
                        assertThat(json).contains(customerDTOList.get(1).getId());
                    });
        }

        @Test
        void deveGerarExcecaoSeNaoEncontrarCliente() throws Exception {
            CustomerDTO customerDTO = NewEntitiesHelper.gerarCustomerInsertRequest();

            when(customerGateway.findCustomer(customerDTO.getId())).thenReturn(null);

            mockMvc.perform(get("/customers/{id}", customerDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andExpect(result -> {
                        String json = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(json).contains("Cliente não encontrado.");
                    });
        }
    }
}
