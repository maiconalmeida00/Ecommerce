package controller;

import exception.BusinessException;
import model.Category;
import model.Product;
import service.CategoryService;
import service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final Scanner scanner;

    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             Scanner scanner) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.scanner = scanner;
    }

    public void menu() {
        int opcao;
        do {
            exibirMenuProdutosECategorias();

            opcao = lerInt();

            try {
                switch (opcao) {
                    case 1 -> listarProdutos();
                    case 2 -> listarProdutosAcimaDe();
                    case 3 -> criarProduto();
                    case 4 -> atualizarProduto();
                    case 5 -> deletarProduto();
                    case 6 -> criarCategoria();
                    case 7 -> atualizarCategoria();
                    case 8 -> deletarCategoria();
                    case 9 -> listarCategorias();
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (BusinessException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void exibirMenuProdutosECategorias() {
        System.out.println("\n========================================");
        System.out.println("        MENU PRODUTOS E CATEGORIAS      ");
        System.out.println("========================================");
        System.out.println(" Produtos:");
        System.out.println("  1) Listar produtos");
        System.out.println("  2) Listar produtos acima de um preço");
        System.out.println("  3) Criar produto");
        System.out.println("  4) Atualizar produto");
        System.out.println("  5) Deletar produto");
        System.out.println("----------------------------------------");
        System.out.println(" Categorias:");
        System.out.println("  6) Criar categoria");
        System.out.println("  7) Atualizar categoria");
        System.out.println("  8) Deletar categoria");
        System.out.println("  9) Listar categorias");
        System.out.println("----------------------------------------");
        System.out.println("  0) Voltar");
        System.out.println("----------------------------------------");
        System.out.print("Digite a opção: ");
    }

    private void listarProdutos() {
        List<Product> produtos = productService.listarTodosOrdenadosPorPreco();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            produtos.forEach(System.out::println);
        }
    }

    private void listarProdutosAcimaDe() {
        System.out.print("Informe o preço mínimo: ");
        BigDecimal preco = lerBigDecimal();
        List<Product> produtos = productService.filtrarPorPrecoMinimo(preco);
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado com preço >= " + preco);
        } else {
            produtos.forEach(System.out::println);
        }
    }

    private void criarProduto() {
        System.out.println("\n=== Criar Produto ===");
        String nome = lerTextoObrigatorio("Nome: ");

        String descricao = lerTextoObrigatorio("Descrição: ");

        System.out.print("Preço: ");
        BigDecimal preco = lerBigDecimal();

        System.out.print("Estoque: ");
        int estoque = lerInt();

        String tamanho = lerTextoObrigatorio("Tamanho (P, M, G, etc.): ");

        String cor = lerTextoObrigatorio("Cor: ");

        String genero = lerTextoObrigatorio("Gênero (M/F/U): ");

        System.out.print("ID da categoria: ");
        Long categoriaId = lerLong();

        Category categoria = categoryService.buscarPorId(categoriaId);

        Product p = new Product();
        p.setCategory(categoria);
        p.setName(nome);
        p.setDescription(descricao);
        p.setPrice(preco);
        p.setStock(estoque);
        p.setSize(tamanho);
        p.setColor(cor);
        p.setGender(genero);

        Product salvo = productService.criarProduto(p);
        System.out.println("Produto criado: " + salvo);
    }

    private void atualizarProduto() {
        System.out.println("\n=== Atualizar Produto ===");
        System.out.print("ID do produto: ");
        Long id = lerLong();

        Product existente = productService.buscarPorId(id);
        System.out.println("Produto atual: " + existente);

        System.out.print("Novo nome (ENTER para manter): ");
        String nome = scanner.nextLine();
        if (!nome.isBlank()) existente.setName(nome);

        System.out.print("Nova descrição (ENTER para manter): ");
        String descricao = scanner.nextLine();
        if (!descricao.isBlank()) existente.setDescription(descricao);

        System.out.print("Novo preço (ENTER para manter): ");
        BigDecimal novoPreco = lerBigDecimalOpcional();
        if (novoPreco != null) existente.setPrice(novoPreco);

        System.out.print("Novo estoque (ENTER para manter): ");
        Integer novoEstoque = lerIntOpcional();
        if (novoEstoque != null) existente.setStock(novoEstoque);

        System.out.print("Novo tamanho (ENTER para manter): ");
        String tamanho = scanner.nextLine();
        if (!tamanho.isBlank()) existente.setSize(tamanho);

        System.out.print("Nova cor (ENTER para manter): ");
        String cor = scanner.nextLine();
        if (!cor.isBlank()) existente.setColor(cor);

        System.out.print("Novo gênero (ENTER para manter): ");
        String genero = scanner.nextLine();
        if (!genero.isBlank()) existente.setGender(genero);

        System.out.print("Nova categoria ID (ENTER para manter): ");
        Long novaCategoriaId = lerLongOpcional();
        if (novaCategoriaId != null) {
            Category categoria = categoryService.buscarPorId(novaCategoriaId);
            existente.setCategory(categoria);
        }

        Product atualizado = productService.atualizarProduto(existente);
        System.out.println("Produto atualizado: " + atualizado);
    }

    private void deletarProduto() {
        System.out.println("\n=== Deletar Produto ===");
        System.out.print("ID do produto: ");
        Long id = lerLong();

        productService.deletarProduto(id);
        System.out.println("Produto deletado com sucesso.");
    }

    private void criarCategoria() {
        System.out.println("\n=== Criar Categoria ===");
        String nome = lerTextoObrigatorio("Nome da categoria: ");

        System.out.print("Descrição da categoria (opcional): ");
        String descricao = scanner.nextLine();

        Category categoria = new Category();
        categoria.setName(nome);
        categoria.setDescription(descricao == null || descricao.isBlank() ? null : descricao.trim());

        Category salva = categoryService.criar(categoria);
        System.out.println("Categoria criada: " + salva);
    }

    private void atualizarCategoria() {
        System.out.println("\n=== Atualizar Categoria ===");
        System.out.print("ID da categoria: ");
        Long id = lerLong();

        Category existente = categoryService.buscarPorId(id);
        System.out.println("Categoria atual: " + existente);

        System.out.print("Novo nome (ENTER para manter): ");
        String nome = scanner.nextLine();
        if (!nome.isBlank()) existente.setName(nome.trim());

        System.out.print("Nova descrição (ENTER para manter): ");
        String descricao = scanner.nextLine();
        if (!descricao.isBlank()) existente.setDescription(descricao.trim());

        Category atualizada = categoryService.atualizar(existente);
        System.out.println("Categoria atualizada: " + atualizada);
    }

    private void deletarCategoria() {
        System.out.println("\n=== Deletar Categoria ===");
        System.out.print("ID da categoria: ");
        Long id = lerLong();

        categoryService.deletar(id);
        System.out.println("Categoria deletada com sucesso.");
    }

    private void listarCategorias() {
        List<Category> categorias = categoryService.listarTodas();
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
        } else {
            categorias.forEach(System.out::println);
        }
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

    private BigDecimal lerBigDecimal() {
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                System.out.print("Valor obrigatório. Informe um número decimal: ");
                continue;
            }

            try {
                return new BigDecimal(entrada.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Informe um número decimal: ");
            }
        }
    }

    private Integer lerIntOpcional() {
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                return null;
            }

            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Informe um número inteiro (ou ENTER para manter): ");
            }
        }
    }

    private Long lerLongOpcional() {
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                return null;
            }

            try {
                return Long.parseLong(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Informe um número (ou ENTER para manter): ");
            }
        }
    }

    private BigDecimal lerBigDecimalOpcional() {
        while (true) {
            String entrada = scanner.nextLine();
            if (entrada == null || entrada.isBlank()) {
                return null;
            }

            try {
                return new BigDecimal(entrada.trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Informe um número decimal (ou ENTER para manter): ");
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
}

