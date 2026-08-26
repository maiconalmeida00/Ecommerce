package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private Long id;
    private Customer customer;
    private String status;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private List<OrderItem> items = new ArrayList<>();
    private boolean active; 

    public Order() {
        this.active = true;
    }

    public Order(Long id, Customer customer, String status,
                 LocalDateTime orderDate, BigDecimal totalAmount,
                 String shippingAddress) {
        this.id = id;
        this.customer = customer;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.active = true;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        Long customerId = customer != null ? customer.getId() : null;
        String customerName = customer != null ? customer.getName() : null;
        int itensCount = items != null ? items.size() : 0;

        return "Order{" +
                "id=" + id +
                ", clienteId=" + customerId +
                ", clienteNome='" + customerName + '\'' +
                ", status='" + status + '\'' +
                ", dataPedido=" + orderDate +
                ", total=" + totalAmount +
                ", endereco='" + shippingAddress + '\'' +
                ", qtdItens=" + itensCount +
                ", ativo=" + active +
                '}';
    }
}