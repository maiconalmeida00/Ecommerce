import controller.CustomerController;
import controller.OrderController;
import controller.ProductController;
import repository.CustomerRepository;
import repository.OrderItemRepository;
import repository.OrderRepository;
import repository.ProductRepository;
import repository.impl.*;
import service.CategoryService;
import service.CustomerService;
import service.OrderService;
import service.ProductService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        var customerRepo = new JdbcCustomerRepository();
        var categoryRepo = new JdbcCategoryRepository();
        var productRepo = new JdbcProductRepository();
        var orderRepo = new JdbcOrderRepository();
        var orderItemRepo = new JdbcOrderItemRepository();

        var customerService = new CustomerService(customerRepo, orderRepo);
        var categoryService = new CategoryService(categoryRepo, productRepo);
        var productService = new ProductService(productRepo, orderItemRepo);
        var orderService = new OrderService(orderRepo, orderItemRepo, productRepo);

        var customerController = new CustomerController(customerService, sc);
        var productController = new ProductController(productService, categoryService, sc);
        var orderController = new OrderController(orderService, customerService, productService, sc);

        int opcao;
        do {
            exibirMenuPrincipal();

            opcao = lerOpcaoMenu(sc);

            switch (opcao) {
                case 1 -> customerController.menu();
                case 2 -> productController.menu();
                case 3 -> orderController.menu();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        sc.close();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n========================================");
        System.out.println("            MENU PRINCIPAL              ");
        System.out.println("========================================");
        System.out.println("  1) Clientes");
        System.out.println("  2) Produtos e categorias");
        System.out.println("  3) Pedidos");
        System.out.println("----------------------------------------");
        System.out.println("  0) Sair");
        System.out.println("----------------------------------------");
        System.out.print("Digite a opção: ");
    }

    private static int lerOpcaoMenu(Scanner sc) {
        while (true) {
            String entrada = sc.nextLine();
            if (entrada == null || entrada.isBlank()) {
                System.out.print("Opção obrigatória. Informe um número: ");
                continue;
            }

            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.print("Opção inválida. Informe um número: ");
            }
        }
    }
}