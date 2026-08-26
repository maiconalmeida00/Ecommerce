package model;

import java.math.BigDecimal;

public class Product {

    private Long id;
    private Category category;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private String size;
    private String color;
    private String gender;
    private boolean active;

    public Product() {
        this.active = true;
    }

    public Product(Long id, Category category, String name,
                   String description, BigDecimal price, int stock,
                   String size, String color, String gender) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.size = size;
        this.color = color;
        this.gender = gender;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        Long categoryId = category != null ? category.getId() : null;
        String categoryName = category != null ? category.getName() : null;

        return "Product{" +
            "id=" + id +
            ", categoriaId=" + categoryId +
            ", categoriaNome='" + categoryName + '\'' +
            ", nome='" + name + '\'' +
            ", descricao='" + description + '\'' +
            ", preco=" + price +
            ", estoque=" + stock +
            ", tamanho='" + size + '\'' +
            ", cor='" + color + '\'' +
            ", genero='" + gender + '\'' +
                ", ativo=" + active +
            '}';
    }
}
