package controller;

import model.Customer;
import model.Order;
import model.OrderItem;
import model.Product;
import service.CustomerService;
import service.OrderService;
import service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final Scanner scanner;

    public OrderController(OrderService orderService,
                           CustomerService customerService,
                           ProductService productService,
                           Scanner scanner) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.productService = productService;
        this.scanner = scanner;
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n=== Menu Pedido ===");
            System.out.println("1 - Listar pedidos");
            System.out.println("2 - Listar pedidos por cliente");
            System.out.println("3 - Criar pedido");
            System.out.println("4 - Atualizar status do pedido");
            System.out.println("5 - Deletar pedido");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = lerInt();

            switch (opcao) {
                case 1 -> listarPedidos();
                case 2 -> listarPorCliente();
                case 3 -> criarPedido();
                case 4 -> atualizarStatus();
                case 5 -> deletarPedido();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void listarPedidos() {
        List<Order> pedidos = orderService.listarTodos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
        } else {
            pedidos.forEach(System.out::println);
        }
    }

    private void listarPorCliente() {
        System.out.print("ID do cliente: ");
        Long clienteId = lerLong();

        List<Order> pedidos = orderService.listarPorCliente(clienteId);
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado para o cliente " + clienteId);
        } else {
            pedidos.forEach(System.out::println);
        }
    }

    private void criarPedido() {
        System.out.println("\n=== Criar Pedido ===");
        System.out.print("ID do cliente: ");
        Long clienteId = lerLong();

        Customer customer = customerService.buscarPorId(clienteId);

        System.out.print("Endereço de entrega: ");
        String endereco = scanner.nextLine();

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setShippingAddress(endereco);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        while (true) {
            System.out.print("ID do produto (ou 0 para finalizar): ");
            Long produtoId = lerLong();
            if (produtoId == 0L) {
                break;
            }

            Product product = productService.buscarPorId(produtoId);

            System.out.print("Quantidade: ");
            int quantidade = lerInt();

            BigDecimal priceAtPurchase = product.getPrice();
            BigDecimal subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(quantidade));
            total = total.add(subtotal);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantidade);
            item.setPriceAtPurchase(priceAtPurchase);

            items.add(item);
        }

        order.setTotalAmount(total);
        items.forEach(order::addItem);

        Order salvo = orderService.criar(order);
        System.out.println("Pedido criado: " + salvo);
    }

    private void atualizarStatus() {
        System.out.println("\n=== Atualizar Status do Pedido ===");
        System.out.print("ID do pedido: ");
        Long pedidoId = lerLong();

        Order existente = orderService.buscarPorId(pedidoId);
        System.out.println("Pedido atual: " + existente);

        System.out.print("Novo status (ex.: PENDING, PAID, CANCELLED): ");
        String status = scanner.nextLine();

        existente.setStatus(status);
        Order atualizado = orderService.atualizar(existente);

        System.out.println("Pedido atualizado: " + atualizado);
    }

    private void deletarPedido() {
        System.out.println("\n=== Deletar Pedido ===");
        System.out.print("ID do pedido: ");
        Long pedidoId = lerLong();

        orderService.deletar(pedidoId);
        System.out.println("Pedido deletado com sucesso.");
    }

    private int lerInt() {
        int valor = Integer.parseInt(scanner.nextLine());
        return valor;
    }

    private Long lerLong() {
        Long valor = Long.parseLong(scanner.nextLine());
        return valor;
    }
}

