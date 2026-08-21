package repository;

import model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(Long categoryId);
    void deleteById(Long id);
    int countByCategory(Long categoryId);
}
