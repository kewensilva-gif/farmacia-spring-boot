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
        if (!isValid(sale)) {
            throw new IllegalArgumentException("Dados da venda inválidos");
        }
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
        if (!isValid(saleDetails)) {
            throw new IllegalArgumentException("Dados da venda inválidos");
        }

        Sale sale = saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Venda não encontrada"));
        sale.setTotalPrice(saleDetails.getTotalPrice());
        sale.setDiscount(saleDetails.getDiscount());
        sale.setPaymentMethod(saleDetails.getPaymentMethod());

        return saleRepository.save(sale);
    }

    public void deleteById(Long id) {
        saleRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }

    public Boolean isValid(Sale sale) {
        if (sale.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (sale.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (sale.getPaymentMethod() == null) {
            return false;
        }
        if (sale.getDiscount().compareTo(sale.getTotalPrice()) >= 0) {
            return false;
        }
        return true;
    }

    public void recalculateTotal(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        BigDecimal total = sale.getSaleProducts().stream()
            .map(sp -> sp.getUnitPrice().multiply(BigDecimal.valueOf(sp.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        sale.setTotalPrice(total.subtract(sale.getDiscount()));
        saleRepository.save(sale);
    }
}
