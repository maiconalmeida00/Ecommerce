package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.Order;
import model.OrderItem;
import model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import repository.OrderItemRepository;

public class JdbcOrderItemRepository implements OrderItemRepository {

    public OrderItem save(OrderItem item) {
        if (item.getId() == null) {
            return insert(item);
        } else {
            return update(item);
        }
    }

    private OrderItem insert(OrderItem item) {
        String sql = "INSERT INTO order_items " +
                "(order_id, product_id, quantity, price_at_purchase) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatement(ps, item, false);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getLong(1));
                }
            }

            return item;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao inserir item de pedido", e);
        }
    }

    private OrderItem update(OrderItem item) {
        String sql = "UPDATE order_items SET " +
                "order_id = ?, product_id = ?, quantity = ?, price_at_purchase = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatement(ps, item, true);
            ps.executeUpdate();

            return item;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar item de pedido", e);
        }
    }

    private void prepareStatement(PreparedStatement ps, OrderItem item, boolean includeId) throws SQLException {
        Long orderId = item.getOrder() != null ? item.getOrder().getId() : null;
        Long productId = item.getProduct() != null ? item.getProduct().getId() : null;

        ps.setLong(1, orderId);
        ps.setLong(2, productId);
        ps.setInt(3, item.getQuantity());

        BigDecimal price = item.getPriceAtPurchase() != null ? item.getPriceAtPurchase() : BigDecimal.ZERO;
        ps.setBigDecimal(4, price);

        if (includeId) {
            ps.setLong(5, item.getId());
        }
    }

    public List<OrderItem> findByOrder(Long orderId) {
        String sql = "SELECT id, order_id, product_id, quantity, price_at_purchase " +
                "FROM order_items WHERE order_id = ?";

        List<OrderItem> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar itens do pedido", e);
        }
    }

    public void deleteByOrder(Long orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar itens do pedido", e);
        }
    }

    @Override
    public int countByProduct(Long productId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE product_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao contar itens por produto", e);
        }
    }

    private OrderItem map(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();

        item.setId(rs.getLong("id"));

        Order order = new Order();
        order.setId(rs.getLong("order_id"));
        item.setOrder(order);

        Product product = new Product();
        product.setId(rs.getLong("product_id"));
        item.setProduct(product);

        item.setQuantity(rs.getInt("quantity"));
        item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));

        return item;
    }
}