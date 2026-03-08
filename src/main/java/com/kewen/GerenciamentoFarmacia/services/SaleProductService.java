package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Product;
import com.kewen.GerenciamentoFarmacia.entities.SaleProduct;
import com.kewen.GerenciamentoFarmacia.repositories.SaleProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SaleProductService {
    @Autowired
    private SaleProductRepository saleProductRepository;
    @Autowired
    private ProductService productRepository;
    @Autowired
    private SaleService saleService;

    public SaleProduct save(SaleProduct saleProduct) {
        if (!isValid(saleProduct)) {
            throw new IllegalArgumentException("Dados do produto da venda inválidos");
        }

        return saleProductRepository.save(saleProduct);
    }

    public Optional<SaleProduct> findById(Long id) {
        return saleProductRepository.findById(id);
    }

    public List<SaleProduct> findAll() {
        return saleProductRepository.findAll();
    }

    public List<SaleProduct> findBySaleId(Long saleId) {
        return saleProductRepository.findBySaleId(saleId);
    }

    public List<SaleProduct> findByProductId(Long productId) {
        return saleProductRepository.findByProductId(productId);
    }

    public SaleProduct updateQuantity(Long id, Long newQuantity) {
        SaleProduct saleProduct = saleProductRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        Product product = saleProduct.getProduct();
        Long oldQuantity = saleProduct.getQuantity();
        Long diff = newQuantity - oldQuantity;

        if (diff > 0 && product.getStockQuantity() < diff) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }

        product.setStockQuantity((int) (product.getStockQuantity() - diff));
        productRepository.save(product);

        saleProduct.setQuantity(newQuantity);
        SaleProduct updated = saleProductRepository.save(saleProduct);

        saleService.recalculateTotal(saleProduct.getSale().getId());

        return updated;
    }
    public void deleteById(Long id) {
        saleProductRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return saleProductRepository.existsById(id);
    }

    public Boolean isValid(SaleProduct saleProduct) {
        if (saleProduct.getQuantity() <= 0) {
            return false;
        }
        if (saleProduct.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        return true;
    }
}
