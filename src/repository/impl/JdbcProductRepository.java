package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.Category;
import model.Product;
import repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductRepository implements ProductRepository {

    @Override
    public Product save(Product product) {
        if (product.getId() == null) return insert(product);
        return update(product);
    }

    private Product insert(Product p) {
        String sql = "INSERT INTO products (category_id, name, description, price, stock, size, color, gender, active)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            prepare(ps, p);
            ps.setBoolean(9, p.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }
            return p;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao inserir produto", e);
        }
    }

    private Product update(Product p) {
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, "
            + "price = ?, stock = ?, size = ?, color = ?, gender = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            prepare(ps, p);
            ps.setBoolean(9, p.isActive());
            ps.setLong(10, p.getId());

            ps.executeUpdate();
            return p;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar produto", e);
        }
    }

    private void prepare(PreparedStatement ps, Product p) throws SQLException {
        ps.setLong(1, p.getCategory().getId());
        ps.setString(2, p.getName());
        ps.setString(3, p.getDescription());
        ps.setBigDecimal(4, p.getPrice());
        ps.setInt(5, p.getStock());
        ps.setString(6, p.getSize());
        ps.setString(7, p.getColor());
        ps.setString(8, p.getGender());
    }

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT p.id, p.name, p.description, p.price, p.stock, p.size, p.color, p.gender, "
                + "c.id as category_id, c.name as category_name, c.description as category_description, p.active "
                + "FROM products p JOIN categories c ON p.category_id = c.id WHERE p.id = ? AND p.active = 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar produto por ID", e);
        }
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT p.id, p.name, p.description, p.price, p.stock, p.size, p.color, p.gender, "
                + "c.id as category_id, c.name as category_name, c.description as category_description, p.active "
                + "FROM products p JOIN categories c ON p.category_id = c.id WHERE p.active = 1";

        List<Product> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar produtos", e);
        }
    }

    @Override
    public List<Product> findByCategory(Long categoryId) {
        String sql = "SELECT p.id, p.name, p.description, p.price, p.stock, p.size, p.color, p.gender, "
                + "c.id as category_id, c.name as category_name, c.description as category_description, p.active "
                + "FROM products p JOIN categories c ON p.category_id = c.id WHERE c.id = ? AND p.active = 1";

        List<Product> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar produtos por categoria", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "UPDATE products SET active = 0 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar produto", e);
        }
    }

    @Override
    public int countByCategory(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao contar produtos por categoria", e);
        }
    }

    private Product map(ResultSet rs) throws SQLException {
        Category c = new Category(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getString("category_description")
        );

        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setCategory(c);
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setSize(rs.getString("size"));
        p.setColor(rs.getString("color"));
        p.setGender(rs.getString("gender"));
        p.setActive(rs.getBoolean("active"));
        return p;
    }
}