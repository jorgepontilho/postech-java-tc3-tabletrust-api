package com.postech.tabletrust.interfaces;

import com.postech.tabletrust.dto.CustomerDTO;
import com.postech.tabletrust.entity.Customer;

import java.util.List;

public interface ICustomerGateway {
    public Customer createCustomer(Customer customer);

    public Customer updateCustomer(Customer customer);

    public boolean deleteCustomer(String strId);

    public Customer findCustomer(String strId);

    public List<Customer> listAllCustomers();
}
