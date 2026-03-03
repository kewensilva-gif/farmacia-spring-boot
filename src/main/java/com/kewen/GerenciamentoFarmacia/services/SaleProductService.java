package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.SaleProduct;
import com.kewen.GerenciamentoFarmacia.repositories.SaleProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SaleProductService {

    @Autowired
    private SaleProductRepository saleProductRepository;

    public SaleProduct save(SaleProduct saleProduct) {
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

    public SaleProduct update(Long id, SaleProduct saleProductDetails) {
        return saleProductRepository.findById(id).map(saleProduct -> {
            saleProduct.setSale(saleProductDetails.getSale());
            saleProduct.setProduct(saleProductDetails.getProduct());
            saleProduct.setQuantity(saleProductDetails.getQuantity());
            saleProduct.setUnitPrice(saleProductDetails.getUnitPrice());
            return saleProductRepository.save(saleProduct);
        }).orElseThrow(() -> new RuntimeException("Produto da venda não encontrado"));
    }

    public void deleteById(Long id) {
        saleProductRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return saleProductRepository.existsById(id);
    }
}
