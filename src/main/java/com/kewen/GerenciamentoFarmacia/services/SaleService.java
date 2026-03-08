package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Employee;
import com.kewen.GerenciamentoFarmacia.entities.Sale;
import com.kewen.GerenciamentoFarmacia.enums.PaymentMethodEnum;
import com.kewen.GerenciamentoFarmacia.repositories.EmployeeRepository;
import com.kewen.GerenciamentoFarmacia.repositories.SaleRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {
    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleProductService saleProductService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public Sale save(Sale sale) {
        validateForSave(sale);

        sale.getSaleProducts().forEach(item -> {
            item.setSale(sale);
            saleProductService.prepareItem(item);
        });

        sale.setTotalPrice(calculateTotal(sale));

        return saleRepository.save(sale);
    }

    @Transactional
    public Sale update(Long id, Sale saleDetails) {
        validateForUpdate(saleDetails);

        return saleRepository.findById(id).map(sale -> {
            sale.setDiscount(saleDetails.getDiscount());
            sale.setPaymentMethod(saleDetails.getPaymentMethod());
            sale.setTotalPrice(calculateTotal(sale));
            return saleRepository.save(sale);
        }).orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    @Transactional
    public void deleteById(Long id) {
        Sale sale = saleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        saleRepository.delete(sale);
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

    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }

    private void validateForSave(Sale sale) {
        if (sale.getEmployee() == null || sale.getEmployee().getId() == null) {
            throw new IllegalArgumentException("Toda venda precisa de um funcionário responsável");
        }

        Employee employee = employeeRepository.findById(sale.getEmployee().getId())
            .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        if (employee.getTerminationDate() != null) {
            throw new IllegalArgumentException("Funcionário desligado não pode registrar vendas");
        }

        if (sale.getPaymentMethod() == null) {
            throw new IllegalArgumentException("O método de pagamento não pode ser nulo");
        }

        if (sale.getDiscount() == null) {
            throw new IllegalArgumentException("O desconto não pode ser nulo");
        }

        if (sale.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O desconto não pode ser negativo");
        }

        if (sale.getSaleProducts() == null || sale.getSaleProducts().isEmpty()) {
            throw new IllegalArgumentException("A venda deve ter ao menos um item");
        }
    }

    private void validateForUpdate(Sale sale) {
        if (sale.getPaymentMethod() == null) {
            throw new IllegalArgumentException("O método de pagamento não pode ser nulo");
        }

        if (sale.getDiscount() == null) {
            throw new IllegalArgumentException("O desconto não pode ser nulo");
        }

        if (sale.getDiscount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O desconto não pode ser negativo");
        }
    }

    private BigDecimal calculateTotal(Sale sale) {
        if (sale.getSaleProducts() == null || sale.getSaleProducts().isEmpty()) {
            throw new IllegalArgumentException("A venda deve ter ao menos um item");
        }

        BigDecimal subtotal = sale.getSaleProducts().stream()
            .map(sp -> sp.getUnitPrice().multiply(BigDecimal.valueOf(sp.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal.subtract(sale.getDiscount());

        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O total da venda após desconto deve ser maior que zero");
        }

        return total;
    }
}