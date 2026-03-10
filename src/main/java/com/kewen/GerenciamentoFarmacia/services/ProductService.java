package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Product;
import com.kewen.GerenciamentoFarmacia.repositories.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryService categoryService;

    public Product save(Product product) {
        validateForSave(product);
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findByIdAndEnabledTrue(id);
    }

    public List<Product> findAll() {
        return productRepository.findByEnabledTrue();
    }

    public Optional<Product> findByBarcode(String barcode) {
        return productRepository.findByBarcodeAndEnabledTrue(barcode);
    }

    public List<Product> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndEnabledTrue(name);
    }

    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndEnabledTrue(categoryId);
    }

    public List<Product> findExpiredProducts() {
        return productRepository.findByExpirationDateBeforeAndEnabledTrue(LocalDate.now());
    }

    public List<Product> findLowStockProducts(Integer quantity) {
        return productRepository.findByStockQuantityLessThanAndEnabledTrue(quantity);
    }

    public Product update(Long id, Product productDetails) {
        validateForUpdate(id, productDetails);

        return productRepository.findByIdAndEnabledTrue(id).map(product -> {
            product.setName(productDetails.getName());
            product.setUnitPrice(productDetails.getUnitPrice());
            product.setBarcode(productDetails.getBarcode());
            product.setExpirationDate(productDetails.getExpirationDate());
            product.setPathImage(productDetails.getPathImage());
            product.setCategory(productDetails.getCategory());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void debitStock(Long id, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade a debitar deve ser positiva");
        }

        Product product = productRepository.findByIdAndEnabledTrue(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }
        
        int newStock = product.getStockQuantity() - quantity;

        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    public void addStock(Long id, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade a adicionar deve ser positiva");
        }

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        int newStock = product.getStockQuantity() + quantity;

        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    public void deleteById(Long id) {
        Product product = productRepository.findByIdAndEnabledTrue(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setEnabled(false);
        productRepository.save(product);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    private void validateForSave(Product product) {
        if (product.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço unitário não pode ser zero ou negativo");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa");
        }
        if (product.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de validade não pode ser anterior à data atual");
        }
        if (categoryService.findById(product.getCategory().getId()).isEmpty()) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }
        if (productRepository.findByBarcode(product.getBarcode()).isPresent()) {
            throw new IllegalArgumentException("Código de barras já cadastrado");
        }
    }

    private void validateForUpdate(Long id, Product product) {
        if (product.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço unitário não pode ser zero ou negativo");
        }
        if (product.getExpirationDate() != null && product.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de validade não pode ser anterior à data atual");
        }
        if (categoryService.findById(product.getCategory().getId()).isEmpty()) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }
        productRepository.findByBarcode(product.getBarcode())
            .ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("Código de barras já cadastrado");
                }
            });
    }
}
