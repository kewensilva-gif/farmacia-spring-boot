package com.kewen.GerenciamentoFarmacia.controllers;

import com.kewen.GerenciamentoFarmacia.entities.Sale;
import com.kewen.GerenciamentoFarmacia.enums.PaymentMethodEnum;
import com.kewen.GerenciamentoFarmacia.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping
    public ResponseEntity<List<Sale>> findAll() {
        return ResponseEntity.ok(saleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> findById(@PathVariable Long id) {
        return saleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Sale sale) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(saleService.save(sale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Sale sale) {
        try {
            return ResponseEntity.ok(saleService.update(id, sale));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            saleService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/payment-method")
    public ResponseEntity<List<Sale>> findByPaymentMethod(@RequestParam PaymentMethodEnum paymentMethod) {
        return ResponseEntity.ok(saleService.findByPaymentMethod(paymentMethod));
    }

    @GetMapping("/search/price-greater")
    public ResponseEntity<List<Sale>> findByPriceGreaterThan(@RequestParam BigDecimal price) {
        return ResponseEntity.ok(saleService.findByPriceGreaterThan(price));
    }

    @GetMapping("/search/price-less")
    public ResponseEntity<List<Sale>> findByPriceLessThan(@RequestParam BigDecimal price) {
        return ResponseEntity.ok(saleService.findByPriceLessThan(price));
    }
}
