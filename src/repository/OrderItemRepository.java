package repository;

import model.OrderItem;

import java.util.List;

public interface OrderItemRepository {

    OrderItem save(OrderItem item);
    List<OrderItem> findByOrder(Long orderId);
    int countByProduct(Long productId);
    void deleteByOrder(Long orderId);
}
