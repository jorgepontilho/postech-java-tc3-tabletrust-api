package com.postech.tabletrust.gateways;

import com.postech.tabletrust.dto.CustomerDTO;
import com.postech.tabletrust.entity.Customer;
import com.postech.tabletrust.exception.NotFoundException;
import com.postech.tabletrust.interfaces.ICustomerGateway;
import com.postech.tabletrust.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class CustomerGateway implements ICustomerGateway {
    private final CustomerRepository customerRepository;

    public CustomerGateway(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public boolean deleteCustomer(String strId) {
        try {
            UUID uuid = UUID.fromString(strId);
            customerRepository.deleteById(uuid);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Customer findCustomer(String strId) {
        try {
            UUID uuid = UUID.fromString(strId);
            return customerRepository.findById(uuid).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Customer> listAllCustomers() {
        return customerRepository.findAll();
    }
}
