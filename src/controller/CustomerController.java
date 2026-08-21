package controller;

import model.Customer;
import service.CustomerService;

import java.util.List;
import java.util.Scanner;

public class CustomerController {

    private final CustomerService customerService;
    private final Scanner scanner;

    public CustomerController(CustomerService customerService, Scanner scanner) {
        this.customerService = customerService;
        this.scanner = scanner;
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n=== Menu Cliente ===");
            System.out.println("1 - Listar clientes");
            System.out.println("2 - Buscar clientes por nome (contém)");
            System.out.println("3 - Criar cliente");
            System.out.println("4 - Atualizar cliente");
            System.out.println("5 - Deletar cliente");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = lerInt();

            switch (opcao) {
                case 1 -> listarClientes();
                case 2 -> buscarPorNome();
                case 3 -> criarCliente();
                case 4 -> atualizarCliente();
                case 5 -> deletarCliente();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void listarClientes() {
        List<Customer> clientes = customerService.listarTodos();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            clientes.forEach(System.out::println);
        }
    }

    private void buscarPorNome() {
        System.out.print("Informe um trecho do nome: ");
        String trecho = scanner.nextLine();
        List<Customer> clientes = customerService.buscarPorNomeContendo(trecho);
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado com '" + trecho + "'.");
        } else {
            clientes.forEach(System.out::println);
        }
    }

    private void criarCliente() {
        System.out.println("\n=== Criar Cliente ===");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Senha (já em hash, ou texto só para teste): ");
        String senhaHash = scanner.nextLine();

        System.out.print("CPF (opcional): ");
        String cpf = scanner.nextLine();

        System.out.print("Telefone (opcional): ");
        String phone = scanner.nextLine();

        Customer c = new Customer();
        c.setName(nome);
        c.setEmail(email);
        c.setPasswordHash(senhaHash);
        c.setCpf(cpf.isBlank() ? null : cpf);
        c.setPhone(phone.isBlank() ? null : phone);

        Customer salvo = customerService.criar(c);
        System.out.println("Cliente criado: " + salvo);
    }

    private void atualizarCliente() {
        System.out.println("\n=== Atualizar Cliente ===");
        System.out.print("ID do cliente: ");
        Long id = lerLong();

        Customer existente = customerService.buscarPorId(id);
        System.out.println("Cliente atual: " + existente);

        System.out.print("Novo nome (ENTER para manter): ");
        String nome = scanner.nextLine();
        if (!nome.isBlank()) existente.setName(nome);

        System.out.print("Novo email (ENTER para manter): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) existente.setEmail(email);

        System.out.print("Nova senha (ENTER para manter): ");
        String senhaHash = scanner.nextLine();
        if (!senhaHash.isBlank()) existente.setPasswordHash(senhaHash);

        System.out.print("Novo CPF (ENTER para manter): ");
        String cpf = scanner.nextLine();
        if (!cpf.isBlank()) existente.setCpf(cpf);

        System.out.print("Novo telefone (ENTER para manter): ");
        String phone = scanner.nextLine();
        if (!phone.isBlank()) existente.setPhone(phone);

        Customer atualizado = customerService.atualizar(existente);
        System.out.println("Cliente atualizado: " + atualizado);
    }

    private void deletarCliente() {
        System.out.println("\n=== Deletar Cliente ===");
        System.out.print("ID do cliente: ");
        Long id = lerLong();

        customerService.deletar(id);
        System.out.println("Cliente deletado com sucesso.");
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
