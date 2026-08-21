package service;

import exception.BusinessException;
import exception.EntityNotFoundException;
import model.Category;
import repository.CategoryRepository;
import repository.ProductRepository;

import java.util.List;
import java.util.Optional;

public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepo, ProductRepository productRepository) {
        this.categoryRepo = categoryRepo;
        this.productRepository = productRepository;
    }

    public Category criar(Category c) {
        validar(c);
        return categoryRepo.save(c);
    }

    public Category atualizar(Category c) {
        if (c.getId() == null) {
            throw new BusinessException("ID da categoria é obrigatório para atualização");
        }
        validar(c);
        return categoryRepo.save(c);
    }

    public void deletar(Long id) {
        if (productRepository.countByCategory(id) > 0) {
            throw new BusinessException("Não é possível excluir a categoria porque existem produtos vinculados");
        }
        categoryRepo.deleteById(id);
    }

    public Category buscarPorId(Long id) {
        Optional<Category> opt = categoryRepo.findById(id);
        return opt.orElseThrow(() ->
                new EntityNotFoundException("Categoria não encontrada: " + id));
    }

    public List<Category> listarTodas() {
        return categoryRepo.findAll();
    }


    private void validar(Category c) {
        if (c.getName() == null || c.getName().isBlank()) {
            throw new BusinessException("Nome da categoria é obrigatório");
        }
    }
}