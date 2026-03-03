package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Product;
import com.kewen.GerenciamentoFarmacia.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    public List<Product> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> findExpiredProducts() {
        return productRepository.findByExpirationDateBefore(LocalDate.now());
    }

    public List<Product> findLowStockProducts(Integer quantity) {
        return productRepository.findByStockQuantityLessThan(quantity);
    }

    public Product update(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setUnitPrice(productDetails.getUnitPrice());
            product.setBarcode(productDetails.getBarcode());
            product.setStockQuantity(productDetails.getStockQuantity());
            product.setExpirationDate(productDetails.getExpirationDate());
            product.setPathImage(productDetails.getPathImage());
            product.setCategory(productDetails.getCategory());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }
}
