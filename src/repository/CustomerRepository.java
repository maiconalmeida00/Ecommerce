package repository;

import model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<Customer> findByIdIncludingInactive(Long id);
    Optional<Customer> findByEmail(String email);
    List<Customer> findAll();
    List<Customer> findAllInactive();
    void deleteById(Long id);
    void reactivateById(Long id);
}