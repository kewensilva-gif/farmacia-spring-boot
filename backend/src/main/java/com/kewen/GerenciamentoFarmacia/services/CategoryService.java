package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Category;
import com.kewen.GerenciamentoFarmacia.repositories.CategoryRepository;
import com.kewen.GerenciamentoFarmacia.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public Category save(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório");
        }

        if (categoryRepository.existsByNameAndEnabledTrue(category.getName())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome '" + category.getName() + "'");
        }

        return categoryRepository.save(category);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findByIdAndEnabledTrue(id);
    }

    public List<Category> findAll() {
        return categoryRepository.findByEnabledTrue();
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByNameAndEnabledTrue(name);
    }

    public Category update(Long id, Category categoryDetails) {
        return categoryRepository.findByIdAndEnabledTrue(id).map(category -> {
            if (categoryDetails.getName() == null || categoryDetails.getName().isBlank()) {
                throw new IllegalArgumentException("O nome da categoria é obrigatório");
            }

            categoryRepository.findByNameAndEnabledTrue(categoryDetails.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("Já existe uma categoria com o nome '" + categoryDetails.getName() + "'");
                    }
                });

            category.setName(categoryDetails.getName());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    public void deleteById(Long id) {
        Category category = categoryRepository.findByIdAndEnabledTrue(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (productRepository.existsByCategoryIdAndEnabledTrue(id)) {
            throw new IllegalStateException("Categoria não pode ser desativada pois possui produtos ativos vinculados");
        }

        category.setEnabled(false);
        categoryRepository.save(category);
    }

    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
}
