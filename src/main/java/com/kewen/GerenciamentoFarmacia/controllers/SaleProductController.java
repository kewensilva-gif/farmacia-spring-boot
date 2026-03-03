package com.kewen.GerenciamentoFarmacia.controllers;

import com.kewen.GerenciamentoFarmacia.entities.SaleProduct;
import com.kewen.GerenciamentoFarmacia.services.SaleProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-products")
@CrossOrigin(origins = "*")
public class SaleProductController {

    @Autowired
    private SaleProductService saleProductService;

    @GetMapping
    public ResponseEntity<List<SaleProduct>> findAll() {
        return ResponseEntity.ok(saleProductService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleProduct> findById(@PathVariable Long id) {
        return saleProductService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SaleProduct> create(@RequestBody SaleProduct saleProduct) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleProductService.save(saleProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleProduct> update(@PathVariable Long id, @RequestBody SaleProduct saleProduct) {
        try {
            return ResponseEntity.ok(saleProductService.update(id, saleProduct));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (saleProductService.existsById(id)) {
            saleProductService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search/sale/{saleId}")
    public ResponseEntity<List<SaleProduct>> findBySaleId(@PathVariable Long saleId) {
        return ResponseEntity.ok(saleProductService.findBySaleId(saleId));
    }

    @GetMapping("/search/product/{productId}")
    public ResponseEntity<List<SaleProduct>> findByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(saleProductService.findByProductId(productId));
    }
}
