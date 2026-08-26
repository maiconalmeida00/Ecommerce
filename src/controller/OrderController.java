package controller;

import exception.BusinessException;
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
            exibirMenuPedidos();

            opcao = lerInt();

            try {
                switch (opcao) {
                    case 1 -> listarPedidos();
                    case 2 -> listarPorCliente();
                    case 3 -> criarPedido();
                    case 4 -> atualizarStatus();
                    case 5 -> deletarPedido();
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (BusinessException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void exibirMenuPedidos() {
        System.out.println("\n========================================");
        System.out.println("               MENU PEDIDOS             ");
        System.out.println("========================================");
        System.out.println("  1) Listar pedidos");
        System.out.println("  2) Listar pedidos por cliente");
        System.out.println("  3) Criar pedido");
        System.out.println("  4) Atualizar status do pedido");
        System.out.println("  5) Deletar pedido");
        System.out.println("----------------------------------------");
        System.out.println("  0) Voltar");
        System.out.println("----------------------------------------");
        System.out.print("Digite a opção: ");
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

        String endereco = lerTextoObrigatorio("Endereço de entrega: ");

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDENTE");
        order.setShippingAddress(endereco);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        while (true) {
            System.out.print("ID do produto (ou 0 para finalizar o pedido): ");
            Long produtoId = lerLong();
            if (produtoId == 0L) {
                break;
            }

            Product product = productService.buscarPorId(produtoId);

            System.out.print("Quantidade: ");
            int quantidade = lerInt();
            if (quantidade <= 0) {
                System.out.println("Quantidade deve ser maior que zero. Item ignorado.");
                continue;
            }

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

        if (items.isEmpty()) {
            System.out.println("Pedido cancelado: é necessário informar ao menos um item.");
            return;
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

        System.out.print("Novo status (PENDENTE, PAGO, ENVIADO, ENTREGUE, CANCELADO): ");
        String status = lerTextoObrigatorioSemPrompt();

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
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                System.out.print("Valor obrigatório. Informe um número inteiro: ");
                continue;
            }

            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Informe um número inteiro: ");
            }
        }
    }

    private Long lerLong() {
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                System.out.print("Valor obrigatório. Informe um número: ");
                continue;
            }

            try {
                return Long.parseLong(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Informe um número: ");
            }
        }
    }

    private String lerTextoObrigatorio(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine();
            if (entrada != null && !entrada.isBlank()) {
                return entrada.trim();
            }
            System.out.println("Campo obrigatório. Tente novamente.");
        }
    }

    private String lerTextoObrigatorioSemPrompt() {
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada != null && !entrada.isBlank()) {
                return entrada.trim();
            }
            System.out.print("Campo obrigatório. Informe um valor: ");
        }
    }
}

