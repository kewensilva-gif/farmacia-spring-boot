package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Sale;
import com.kewen.GerenciamentoFarmacia.enums.PaymentMethodEnum;
import com.kewen.GerenciamentoFarmacia.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public Sale save(Sale sale) {
        return saleRepository.save(sale);
    }

    public Optional<Sale> findById(Long id) {
        return saleRepository.findById(id);
    }

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public List<Sale> findByPaymentMethod(PaymentMethodEnum paymentMethod) {
        return saleRepository.findByPaymentMethod(paymentMethod);
    }

    public List<Sale> findByPriceGreaterThan(BigDecimal price) {
        return saleRepository.findByTotalPriceGreaterThan(price);
    }

    public List<Sale> findByPriceLessThan(BigDecimal price) {
        return saleRepository.findByTotalPriceLessThan(price);
    }

    public Sale update(Long id, Sale saleDetails) {
        return saleRepository.findById(id).map(sale -> {
            sale.setTotalPrice(saleDetails.getTotalPrice());
            sale.setDiscount(saleDetails.getDiscount());
            sale.setPaymentMethod(saleDetails.getPaymentMethod());
            return saleRepository.save(sale);
        }).orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    public void deleteById(Long id) {
        saleRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }
}
