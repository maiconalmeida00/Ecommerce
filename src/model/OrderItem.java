package model;

import java.math.BigDecimal;

public class OrderItem {

    private Long id;
    private Order order;
    private Product product;
    private int quantity;
    private BigDecimal priceAtPurchase;

    public OrderItem() {
    }

    public OrderItem(Long id, Order order, Product product,
                     int quantity, BigDecimal priceAtPurchase) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    @Override
    public String toString() {
        Long orderId = order != null ? order.getId() : null;
        Long productId = product != null ? product.getId() : null;
        String productName = product != null ? product.getName() : null;

        return "OrderItem{" +
            "id=" + id +
            ", pedidoId=" + orderId +
            ", produtoId=" + productId +
            ", produtoNome='" + productName + '\'' +
            ", quantidade=" + quantity +
            ", precoCompra=" + priceAtPurchase +
            '}';
    }
}
