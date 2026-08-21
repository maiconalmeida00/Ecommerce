package service;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Order;
import model.OrderItem;
import model.Product;
import repository.OrderItemRepository;
import repository.OrderRepository;
import repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo,
                        OrderItemRepository orderItemRepo,
                        ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.productRepo = productRepo;
    }

    public Order criar(Order order) {
        validar(order);

        BigDecimal total = order.getTotalAmount();
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            total = calcularTotal(order);
            order.setTotalAmount(total);
        }

        Order salvo = orderRepo.save(order);

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(salvo);

                Product produto = productRepo.findById(
                        item.getProduct().getId()
                ).orElseThrow(() -> new EntityNotFoundException(
                        "Produto não encontrado para item de pedido: " +
                                item.getProduct().getId()));

                int estoqueAtual = produto.getStock();
                int novoEstoque = estoqueAtual - item.getQuantity();
                if (novoEstoque < 0) {
                    throw new BusinessException("Estoque insuficiente para produto: " + produto.getName());
                }
                produto.setStock(novoEstoque);
                productRepo.save(produto);

                orderItemRepo.save(item);
            }
        }

        return salvo;
    }

    public Order buscarPorId(Long id) {
        Optional<Order> opt = orderRepo.findById(id);
        Order order = opt.orElseThrow(() ->
                new EntityNotFoundException("Pedido não encontrado: " + id));

        List<OrderItem> itens = orderItemRepo.findByOrder(id);
        itens.forEach(order::addItem);

        return order;
    }

    public List<Order> listarTodos() {
        List<Order> pedidos = orderRepo.findAll();
        for (Order o : pedidos) {
            List<OrderItem> itens = orderItemRepo.findByOrder(o.getId());
            itens.forEach(o::addItem);
        }
        return pedidos;
    }

    public List<Order> listarPorCliente(Long customerId) {
        List<Order> pedidos = orderRepo.findByCustomer(customerId);
        for (Order o : pedidos) {
            List<OrderItem> itens = orderItemRepo.findByOrder(o.getId());
            itens.forEach(o::addItem);
        }
        return pedidos;
    }

    public Order atualizar(Order order) {
        if (order.getId() == null) {
            throw new BusinessException("ID do pedido é obrigatório para atualização");
        }

        validar(order);
        BigDecimal total = calcularTotal(order);
        order.setTotalAmount(total);

        return orderRepo.save(order);
    }

    public void deletar(Long id) {
        List<OrderItem> itens = orderItemRepo.findByOrder(id);
        orderItemRepo.deleteByOrder(id);

        orderRepo.deleteById(id);
    }

    private void validar(Order order) {
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new BusinessException("Cliente do pedido é obrigatório");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("Pedido deve possuir pelo menos um item");
        }
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            throw new BusinessException("Status do pedido é obrigatório");
        }
    }

    private BigDecimal calcularTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        if (order.getItems() == null) return total;

        for (OrderItem item : order.getItems()) {
            BigDecimal preco = item.getPriceAtPurchase();
            BigDecimal qtd = BigDecimal.valueOf(item.getQuantity());
            total = total.add(preco.multiply(qtd));
        }
        return total;
    }
}