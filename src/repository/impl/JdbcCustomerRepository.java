package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.Customer;
import repository.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCustomerRepository implements CustomerRepository {

    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            return insert(customer);
        } else {
            return update(customer);
        }
    }

    private Customer insert(Customer customer) {
        String sql = "INSERT INTO customers (name, email, password_hash, cpf, phone, active) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPasswordHash());
            ps.setString(4, customer.getCpf());
            ps.setString(5, customer.getPhone());
            ps.setBoolean(6, customer.isActive()); 

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setId(rs.getLong(1));
                }
            }
            return customer;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao inserir cliente (SQLState=" + e.getSQLState() + ", código=" + e.getErrorCode() + ")",
                    e
            );
        }
    }

    private Customer update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, password_hash = ?, cpf = ?, phone = ?, active = ? "
                + "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPasswordHash());
            ps.setString(4, customer.getCpf());
            ps.setString(5, customer.getPhone());
            ps.setBoolean(6, customer.isActive());
            ps.setLong(7, customer.getId());

            ps.executeUpdate();

            return customer;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar cliente", e);
        }
    }

    @Override
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT id, name, email, password_hash, cpf, phone, active " +
                     "FROM customers WHERE id = ? AND active = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = map(rs);
                    return Optional.of(c);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar cliente por ID", e);
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT id, name, email, password_hash, cpf, phone, active " +
                     "FROM customers WHERE email = ? AND active = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar cliente por e-mail", e);
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT id, name, email, password_hash, cpf, phone, active FROM customers WHERE active = 1";
        List<Customer> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar clientes", e);
        }
        return list;
    }

    @Override
    public void deleteById(Long id) {
        // soft delete: marca como inativo, não remove linha
        String sql = "UPDATE customers SET active = 0 WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao inativar cliente", e);
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPasswordHash(rs.getString("password_hash"));
        c.setCpf(rs.getString("cpf"));
        c.setPhone(rs.getString("phone"));
        c.setActive(rs.getBoolean("active"));
        return c;
    }
}