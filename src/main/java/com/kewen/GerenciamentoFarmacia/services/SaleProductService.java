package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Product;
import com.kewen.GerenciamentoFarmacia.entities.SaleProduct;
import com.kewen.GerenciamentoFarmacia.repositories.SaleProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SaleProductService {
    @Autowired
    private SaleProductRepository saleProductRepository;
    @Autowired
    private ProductService productService;

    public void prepareItem(SaleProduct item) {
        validate(item);

        Product product = productService.findById(item.getProduct().getId())
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (product.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Produto vencido não pode ser vendido: " + product.getName());
        }
        
        productService.debitStock(product.getId(), item.getQuantity().intValue());

        // Manter o preço unitário do produto no item da venda, mesmo que o preço do produto seja atualizado posteriormente
        item.setUnitPrice(product.getUnitPrice());
    }
    
    public void deleteById(Long id) {
        SaleProduct item = saleProductRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        productService.addStock(item.getProduct().getId(), item.getQuantity().intValue());
        saleProductRepository.delete(item);
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

    public boolean existsById(Long id) {
        return saleProductRepository.existsById(id);
    }

    public void validate(SaleProduct saleProduct) {
        if (saleProduct.getQuantity() <= 0) {
            throw new IllegalArgumentException("A quantidade do produto da venda deve ser positiva");
        }
    }
}
