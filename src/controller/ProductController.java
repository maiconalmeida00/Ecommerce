package controller;

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
            System.out.println("\n=== Menu Produto ===");
            System.out.println("1 - Listar produtos");
            System.out.println("2 - Listar produtos acima de um preço");
            System.out.println("3 - Criar produto");
            System.out.println("4 - Atualizar produto");
            System.out.println("5 - Deletar produto");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");

            opcao = lerInt();

            switch (opcao) {
                case 1 -> listarProdutos();
                case 2 -> listarProdutosAcimaDe();
                case 3 -> criarProduto();
                case 4 -> atualizarProduto();
                case 5 -> deletarProduto();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
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
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        System.out.print("Preço: ");
        BigDecimal preco = lerBigDecimal();

        System.out.print("Estoque: ");
        int estoque = lerInt();

        System.out.print("Tamanho (P, M, G, etc.): ");
        String tamanho = scanner.nextLine();

        System.out.print("Cor: ");
        String cor = scanner.nextLine();

        System.out.print("Gênero (M/F/U): ");
        String genero = scanner.nextLine();

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
        String precoStr = scanner.nextLine();
        if (!precoStr.isBlank()) existente.setPrice(new BigDecimal(precoStr));

        System.out.print("Novo estoque (ENTER para manter): ");
        String estoqueStr = scanner.nextLine();
        if (!estoqueStr.isBlank()) existente.setStock(Integer.parseInt(estoqueStr));

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
        String categoriaStr = scanner.nextLine();
        if (!categoriaStr.isBlank()) {
            Long novaCategoriaId = Long.parseLong(categoriaStr);
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

    private int lerInt() {
        int valor = Integer.parseInt(scanner.nextLine());
        return valor;
    }

    private Long lerLong() {
        Long valor = Long.parseLong(scanner.nextLine());
        return valor;
    }

    private BigDecimal lerBigDecimal() {
        String s = scanner.nextLine();
        return new BigDecimal(s.replace(",", "."));
    }
}

