package com.kewen.GerenciamentoFarmacia.entities;

import java.math.BigDecimal;
import java.util.List;

import com.kewen.GerenciamentoFarmacia.converters.PaymentMethodConverter;
import com.kewen.GerenciamentoFarmacia.enums.PaymentMethodEnum;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sale", schema = "public")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discount;

    @Column(name = "payment_method", nullable = false, columnDefinition = "payment_method")
    @Convert(converter = PaymentMethodConverter.class)
    private PaymentMethodEnum paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleProduct> saleProducts;
}