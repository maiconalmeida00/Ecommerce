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

        CustomerRepository customerRepo = new JdbcCustomerRepository();
        var categoryRepo = new JdbcCategoryRepository();
        ProductRepository productRepo = new JdbcProductRepository();
        OrderRepository orderRepo = new JdbcOrderRepository();
        OrderItemRepository orderItemRepo = new JdbcOrderItemRepository();

        var customerService = new CustomerService(customerRepo, orderRepo);
        var categoryService = new CategoryService(categoryRepo, productRepo);
        var productService = new ProductService(productRepo, orderItemRepo);
        var orderService = new OrderService(orderRepo, orderItemRepo, productRepo);

        var customerController = new CustomerController(customerService, sc);
        var productController = new ProductController(productService, categoryService, sc);
        var orderController = new OrderController(orderService, customerService, productService, sc);

        int opcao;
        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Clientes");
            System.out.println("2 - Produtos");
            System.out.println("3 - Pedidos");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(sc.nextLine());

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
}