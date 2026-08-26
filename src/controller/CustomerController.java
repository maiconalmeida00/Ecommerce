package controller;

import exception.BusinessException;
import model.Customer;
import service.CustomerService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            exibirMenuCliente();

            opcao = lerInt();

            try {
                switch (opcao) {
                    case 1 -> listarClientes();
                    case 2 -> buscarPorNome();
                    case 3 -> criarCliente();
                    case 4 -> atualizarCliente();
                    case 5 -> deletarCliente();
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (BusinessException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void exibirMenuCliente() {
        System.out.println("\n========================================");
        System.out.println("              MENU CLIENTES             ");
        System.out.println("========================================");
        System.out.println("  1) Listar clientes");
        System.out.println("  2) Buscar clientes por nome");
        System.out.println("  3) Criar cliente");
        System.out.println("  4) Atualizar cliente");
        System.out.println("  5) Deletar cliente");
        System.out.println("----------------------------------------");
        System.out.println("  0) Voltar");
        System.out.println("----------------------------------------");
        System.out.print("Digite a opção: ");
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
        String trecho = lerTextoObrigatorio("Informe um trecho do nome: ");
        List<Customer> clientes = customerService.buscarPorNomeContendo(trecho);
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado com '" + trecho + "'.");
        } else {
            clientes.forEach(System.out::println);
        }
    }

    private void criarCliente() {
        System.out.println("\n=== Criar Cliente ===");
        String nome = lerTextoObrigatorio("Nome: ");

        String email = lerTextoObrigatorio("Email: ");

        String senha = lerTextoObrigatorio("Senha: ");
        String senhaHash = gerarHashSha256(senha);

        String cpf = lerCpfObrigatorio("CPF (apenas números): ");

        String phone = lerTelefoneObrigatorio("Telefone (apenas números): ");

        Customer c = new Customer();
        c.setName(nome);
        c.setEmail(email);
        c.setPasswordHash(senhaHash);
        c.setCpf(cpf);
        c.setPhone(phone);

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
        String senha = scanner.nextLine();
        if (!senha.isBlank()) existente.setPasswordHash(gerarHashSha256(senha.trim()));

        System.out.print("Novo CPF (ENTER para manter): ");
        String cpf = scanner.nextLine();
        if (!cpf.isBlank()) existente.setCpf(validarEFormatarCpf(cpf));

        System.out.print("Novo telefone (ENTER para manter): ");
        String phone = scanner.nextLine();
        if (!phone.isBlank()) existente.setPhone(validarEFormatarTelefone(phone));

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

    private String lerCpfObrigatorio(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                System.out.println("CPF é obrigatório.");
                continue;
            }

            try {
                return validarEFormatarCpf(entrada);
            } catch (BusinessException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private String lerTelefoneObrigatorio(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                System.out.println("Telefone é obrigatório.");
                continue;
            }

            try {
                return validarEFormatarTelefone(entrada);
            } catch (BusinessException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private String validarEFormatarCpf(String valor) {
        String digitos = apenasDigitos(valor);
        if (digitos.length() != 11) {
            throw new BusinessException("CPF deve conter exatamente 11 números");
        }

        return digitos.substring(0, 3) + "."
                + digitos.substring(3, 6) + "."
            + digitos.substring(6, 9) + "-"
                + digitos.substring(9, 11);
    }

    private String validarEFormatarTelefone(String valor) {
        String digitos = apenasDigitos(valor);
        if (digitos.length() != 11) {
            throw new BusinessException("Telefone deve conter exatamente 11 números (DDD + número)");
        }

        return "(" + digitos.substring(0, 2) + ") "
                + digitos.substring(2, 7) + "-"
                + digitos.substring(7, 11);
    }

    private String apenasDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private String gerarHashSha256(String valor) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("Falha ao gerar hash da senha", e);
        }
    }
}
