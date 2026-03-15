package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.*;
import com.kewen.GerenciamentoFarmacia.enums.PaymentMethodEnum;
import com.kewen.GerenciamentoFarmacia.repositories.EmployeeRepository;
import com.kewen.GerenciamentoFarmacia.repositories.SaleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleProductService saleProductService;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private SaleService saleService;

    private Sale sale;
    private Employee employee;
    private Product product;
    private SaleProduct saleProduct;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setTerminationDate(null);

        product = new Product();
        product.setId(1L);
        product.setName("Paracetamol 500mg");
        product.setUnitPrice(new BigDecimal("12.50"));
        product.setEnabled(true);

        saleProduct = new SaleProduct();
        saleProduct.setId(1L);
        saleProduct.setProduct(product);
        saleProduct.setQuantity(2L);
        saleProduct.setUnitPrice(new BigDecimal("12.50"));

        sale = new Sale();
        sale.setId(1L);
        sale.setEmployee(employee);
        sale.setPaymentMethod(PaymentMethodEnum.PIX);
        sale.setDiscount(BigDecimal.ZERO);
        sale.setSaleProducts(new ArrayList<>(List.of(saleProduct)));
        sale.setEnabled(true);
    }

    // ======================== SAVE ========================

    @Test
    @DisplayName("save - deve salvar venda com sucesso")
    void save_deveSalvarVendaComSucesso() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        Sale result = saleService.save(sale);

        assertThat(result).isNotNull();
        verify(saleProductService).prepareItem(any(SaleProduct.class));
        verify(saleRepository).save(sale);
    }

    @Test
    @DisplayName("save - deve lançar exceção quando funcionário é nulo")
    void save_deveLancarExcecaoQuandoFuncionarioNulo() {
        sale.setEmployee(null);

        assertThatThrownBy(() -> saleService.save(sale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Toda venda precisa de um funcionário responsável");
    }

    @Test
    @DisplayName("save - deve lançar exceção quando funcionário não encontrado")
    void save_deveLancarExcecaoQuandoFuncionarioNaoEncontrado() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.save(sale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Funcionário não encontrado");
    }

    @Test
    @DisplayName("save - deve lançar exceção quando funcionário está desligado")
    void save_deveLancarExcecaoQuandoFuncionarioDesligado() {
        employee.setTerminationDate(java.time.LocalDate.of(2024, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> saleService.save(sale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Funcionário desligado não pode registrar vendas");
    }

    @Test
    @DisplayName("save - deve lançar exceção quando método de pagamento é nulo")
    void save_deveLancarExcecaoQuandoMetodoPagamentoNulo() {
        sale.setPaymentMethod(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> saleService.save(sale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O método de pagamento não pode ser nulo");
    }

    @Test
    @DisplayName("save - deve lançar exceção quando desconto é nulo")
    void save_deveLancarExcecaoQuandoDescontoNulo() {
        sale.setDiscount(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> saleService.save(sale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O desconto não pode ser nulo");
    }

    @Test
    @DisplayName("save - deve lançar exceção quando desconto é negativo")
    void save_deveLancarExcecaoQuandoDescontoNegativo() {
        sale.setDiscount(new BigDecimal("-10.00"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> saleService.save(sale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O desconto não pode ser negativo");
    }

    @Test
    @DisplayName("save - deve lançar exceção quando venda não tem itens")
    void save_deveLancarExcecaoQuandoSemItens() {
        sale.setSaleProducts(new ArrayList<>());
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> saleService.save(sale))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A venda deve ter ao menos um item");
    }

    // ======================== FIND ========================

    @Test
    @DisplayName("findById - deve retornar venda ativa")
    void findById_deveRetornarVendaAtiva() {
        when(saleRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(sale));

        Optional<Sale> result = saleService.findById(1L);

        assertThat(result).isPresent();
        verify(saleRepository).findByIdAndEnabledTrue(1L);
    }

    @Test
    @DisplayName("findById - deve retornar vazio para venda não encontrada")
    void findById_deveRetornarVazioQuandoNaoEncontrada() {
        when(saleRepository.findByIdAndEnabledTrue(99L)).thenReturn(Optional.empty());

        Optional<Sale> result = saleService.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll - deve retornar apenas vendas ativas")
    void findAll_deveRetornarVendasAtivas() {
        when(saleRepository.findByEnabledTrue()).thenReturn(List.of(sale));

        List<Sale> result = saleService.findAll();

        assertThat(result).hasSize(1);
        verify(saleRepository).findByEnabledTrue();
    }

    @Test
    @DisplayName("findByPaymentMethod - deve buscar por método de pagamento com enabled")
    void findByPaymentMethod_deveBuscarPorMetodoPagamentoEEnabled() {
        when(saleRepository.findByPaymentMethodAndEnabledTrue(PaymentMethodEnum.PIX)).thenReturn(List.of(sale));

        List<Sale> result = saleService.findByPaymentMethod(PaymentMethodEnum.PIX);

        assertThat(result).hasSize(1);
        verify(saleRepository).findByPaymentMethodAndEnabledTrue(PaymentMethodEnum.PIX);
    }

    @Test
    @DisplayName("findByPriceGreaterThan - deve buscar vendas com preço maior que")
    void findByPriceGreaterThan_deveBuscarVendasComPrecoMaiorQue() {
        when(saleRepository.findByTotalPriceGreaterThanAndEnabledTrue(new BigDecimal("10.00"))).thenReturn(List.of(sale));

        List<Sale> result = saleService.findByPriceGreaterThan(new BigDecimal("10.00"));

        assertThat(result).hasSize(1);
        verify(saleRepository).findByTotalPriceGreaterThanAndEnabledTrue(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("findByPriceLessThan - deve buscar vendas com preço menor que")
    void findByPriceLessThan_deveBuscarVendasComPrecoMenorQue() {
        when(saleRepository.findByTotalPriceLessThanAndEnabledTrue(new BigDecimal("100.00"))).thenReturn(List.of(sale));

        List<Sale> result = saleService.findByPriceLessThan(new BigDecimal("100.00"));

        assertThat(result).hasSize(1);
        verify(saleRepository).findByTotalPriceLessThanAndEnabledTrue(new BigDecimal("100.00"));
    }

    // ======================== UPDATE ========================

    @Test
    @DisplayName("update - deve atualizar venda existente")
    void update_deveAtualizarVendaExistente() {
        Sale updated = new Sale();
        updated.setPaymentMethod(PaymentMethodEnum.CREDITCARD);
        updated.setDiscount(new BigDecimal("5.00"));

        when(saleRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        Sale result = saleService.update(1L, updated);

        assertThat(result).isNotNull();
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    @DisplayName("update - deve lançar exceção para venda não encontrada")
    void update_deveLancarExcecaoParaVendaNaoEncontrada() {
        Sale updated = new Sale();
        updated.setPaymentMethod(PaymentMethodEnum.PIX);
        updated.setDiscount(BigDecimal.ZERO);

        when(saleRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.update(1L, updated))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venda não encontrada");
    }

    @Test
    @DisplayName("update - deve lançar exceção para método de pagamento nulo")
    void update_deveLancarExcecaoParaMetodoPagamentoNulo() {
        Sale updated = new Sale();
        updated.setPaymentMethod(null);
        updated.setDiscount(BigDecimal.ZERO);

        assertThatThrownBy(() -> saleService.update(1L, updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O método de pagamento não pode ser nulo");
    }

    // ======================== SOFT DELETE ========================

    @Test
    @DisplayName("deleteById - deve desativar venda e restaurar estoque (soft delete)")
    void deleteById_deveDesativarVendaERestaurarEstoque() {
        when(saleRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        saleService.deleteById(1L);

        assertThat(sale.getEnabled()).isFalse();
        verify(saleProductService).restoreStock(saleProduct);
        verify(saleRepository).save(sale);
        verify(saleRepository, never()).delete(any(Sale.class));
    }

    @Test
    @DisplayName("deleteById - deve lançar exceção para venda não encontrada")
    void deleteById_deveLancarExcecaoParaVendaNaoEncontrada() {
        when(saleRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.deleteById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venda não encontrada");
    }
}
