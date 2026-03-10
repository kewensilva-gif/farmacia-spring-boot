package com.kewen.GerenciamentoFarmacia.repositories;

import com.kewen.GerenciamentoFarmacia.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByBarcode(String barcode);
    boolean existsByBarcode(String barcode);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByExpirationDateBefore(LocalDate date);
    List<Product> findByStockQuantityLessThan(Integer quantity);

    // Queries para soft delete
    List<Product> findByEnabledTrue();
    Optional<Product> findByIdAndEnabledTrue(Long id);
    Optional<Product> findByBarcodeAndEnabledTrue(String barcode);
    List<Product> findByNameContainingIgnoreCaseAndEnabledTrue(String name);
    List<Product> findByCategoryIdAndEnabledTrue(Long categoryId);
    List<Product> findByExpirationDateBeforeAndEnabledTrue(LocalDate date);
    List<Product> findByStockQuantityLessThanAndEnabledTrue(Integer quantity);
    boolean existsByCategoryIdAndEnabledTrue(Long categoryId);
}
