package br.com.eboli.models.utils;

import br.com.eboli.models.Customer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomerUtil {

    public static Set<Customer> proccessToIds(Set<Long> customerIds) {
        Set<Customer> customers = new HashSet<>();
        customerIds.stream()
                .map(c -> customers.add(Customer.builder()
                        .id(customerIds.iterator().next())
                        .fullname(null)
                        .cnpj(null)
                        .foundation(null)
                        .registered(null)
                        .build()))
                .collect(Collectors.toList());

        return customers;
    }

    public static Set<Long> proccessToCustomerIds(Set<Customer> customers) {
        Set<Long> ids = new HashSet<>();
        while (customers.iterator().hasNext()) {
            ids.add(customers.iterator().next().getId());
        }
        return ids;
    }

}
