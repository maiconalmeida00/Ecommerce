package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.Customer;
import model.Order;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import repository.OrderRepository;

public class JdbcOrderRepository implements OrderRepository {

    public Order save(Order order) {
        if (order.getId() == null) {
            return insert(order);
        } else {
            return update(order);
        }
    }

    private Order insert(Order order) {
        String sql = "INSERT INTO orders " +
                "(customer_id, status, order_date, total_amount, shipping_address) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatement(ps, order, false);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getLong(1));
                }
            }

            return order;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao inserir pedido", e);
        }
    }

    private Order update(Order order) {
        String sql = "UPDATE orders SET " +
                "customer_id = ?, status = ?, order_date = ?, total_amount = ?, shipping_address = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepareStatement(ps, order, true);
            ps.executeUpdate();

            return order;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar pedido", e);
        }
    }

    private void prepareStatement(PreparedStatement ps, Order order, boolean includeId) throws SQLException {
        Long customerId = order.getCustomer() != null ? order.getCustomer().getId() : null;
        ps.setLong(1, customerId);

        ps.setString(2, order.getStatus());

        LocalDateTime orderDate = order.getOrderDate();
        if (orderDate == null) {
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        } else {
            ps.setTimestamp(3, Timestamp.valueOf(orderDate));
        }

        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        ps.setBigDecimal(4, total);

        ps.setString(5, order.getShippingAddress());

        if (includeId) {
            ps.setLong(6, order.getId());
        }
    }

    public Optional<Order> findById(Long id) {
        String sql = "SELECT id, customer_id, status, order_date, total_amount, shipping_address " +
                "FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar pedido por ID", e);
        }
    }

    public List<Order> findByCustomer(Long customerId) {
        String sql = "SELECT id, customer_id, status, order_date, total_amount, shipping_address " +
                "FROM orders WHERE customer_id = ?";

        List<Order> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar pedidos por cliente", e);
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT id, customer_id, status, order_date, total_amount, shipping_address " +
                "FROM orders";

        List<Order> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar pedidos", e);
        }
    }

    @Override
    public int countByCustomer(Long customerId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE customer_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao contar pedidos por cliente", e);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar pedido", e);
        }
    }

    private Order map(ResultSet rs) throws SQLException {
        Order order = new Order();

        order.setId(rs.getLong("id"));

        Customer customer = new Customer();
        customer.setId(rs.getLong("customer_id"));
        order.setCustomer(customer);

        Timestamp ts = rs.getTimestamp("order_date");
        if (ts != null) {
            order.setOrderDate(ts.toLocalDateTime());
        }

        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setShippingAddress(rs.getString("shipping_address"));

        return order;
    }
}