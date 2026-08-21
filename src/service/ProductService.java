package service;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Product;
import repository.OrderItemRepository;
import repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Product criarProduto(Product product) {
        validar(product);
        return productRepository.save(product);
    }

    public Product atualizarProduto(Product product) {
        if (product.getId() == null) {
            throw new BusinessException("ID do produto é obrigatório para atualização");
        }
        validar(product);
        return productRepository.save(product);
    }

    public void deletarProduto(Long id) {
        if (orderItemRepository.countByProduct(id) > 0) {
            throw new BusinessException("Não é possível excluir o produto porque ele já foi usado em pedidos");
        }
        productRepository.deleteById(id);
    }

    public Product buscarPorId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    public List<Product> listarTodosOrdenadosPorPreco() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getPrice))
                .collect(Collectors.toList());
    }

    public List<Product> filtrarPorPrecoMinimo(BigDecimal precoMinimo) {
        return productRepository.findAll().stream()
                .filter(p -> p.getPrice().compareTo(precoMinimo) >= 0)
                .sorted(Comparator.comparing(Product::getPrice))
                .collect(Collectors.toList());
    }

    public List<Product> buscarPorCategoria(Long categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    private void validar(Product p) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new BusinessException("Nome do produto é obrigatório");
        }
        if (p.getPrice() == null || p.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }
        if (p.getCategory() == null || p.getCategory().getId() == null) {
            throw new BusinessException("Categoria do produto é obrigatória");
        }
    }
}
