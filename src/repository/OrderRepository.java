package repository;

import model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByCustomer(Long customerId);
    List<Order> findAll();
    int countByCustomer(Long customerId);
    void deleteById(Long id); 
}
