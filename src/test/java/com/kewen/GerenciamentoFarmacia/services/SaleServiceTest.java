package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Sale;
import com.kewen.GerenciamentoFarmacia.enums.PaymentMethodEnum;
import com.kewen.GerenciamentoFarmacia.repositories.SaleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

    @InjectMocks
    private SaleService saleService;

    private Sale sale;

    @BeforeEach
    void setUp() {
        sale = new Sale();
        sale.setId(1L);
        sale.setTotalPrice(new BigDecimal("150.00"));
        sale.setDiscount(new BigDecimal("10.00"));
        sale.setPaymentMethod(PaymentMethodEnum.CREDITCARD);
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save - deve salvar e retornar a venda")
    void save_deveSalvarERetornarVenda() {
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);

        Sale result = saleService.save(sale);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotalPrice()).isEqualByComparingTo("150.00");
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethodEnum.CREDITCARD);
        verify(saleRepository, times(1)).save(sale);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById - deve retornar Optional com venda quando encontrada")
    void findById_deveRetornarVendaQuandoEncontrada() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        Optional<Sale> result = saleService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getDiscount()).isEqualByComparingTo("10.00");
        verify(saleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrada")
    void findById_deveRetornarVazioQuandoNaoEncontrada() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Sale> result = saleService.findById(99L);

        assertThat(result).isEmpty();
        verify(saleRepository, times(1)).findById(99L);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll - deve retornar lista de vendas")
    void findAll_deveRetornarListaDeVendas() {
        Sale outra = new Sale();
        outra.setId(2L);
        outra.setTotalPrice(new BigDecimal("50.00"));
        outra.setDiscount(new BigDecimal("0.00"));
        outra.setPaymentMethod(PaymentMethodEnum.PIX);

        when(saleRepository.findAll()).thenReturn(List.of(sale, outra));

        List<Sale> result = saleService.findAll();

        assertThat(result).hasSize(2);
        verify(saleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - deve retornar lista vazia quando não há vendas")
    void findAll_deveRetornarListaVazia() {
        when(saleRepository.findAll()).thenReturn(List.of());

        List<Sale> result = saleService.findAll();

        assertThat(result).isEmpty();
        verify(saleRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // findByPaymentMethod
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByPaymentMethod - deve retornar vendas pelo método de pagamento")
    void findByPaymentMethod_deveRetornarVendasPorMetodoDePagamento() {
        when(saleRepository.findByPaymentMethod(PaymentMethodEnum.CREDITCARD)).thenReturn(List.of(sale));

        List<Sale> result = saleService.findByPaymentMethod(PaymentMethodEnum.CREDITCARD);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPaymentMethod()).isEqualTo(PaymentMethodEnum.CREDITCARD);
        verify(saleRepository, times(1)).findByPaymentMethod(PaymentMethodEnum.CREDITCARD);
    }

    // -------------------------------------------------------------------------
    // findByPriceGreaterThan / findByPriceLessThan
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByPriceGreaterThan - deve retornar vendas com preço maior que o informado")
    void findByPriceGreaterThan_deveRetornarVendas() {
        BigDecimal preco = new BigDecimal("100.00");
        when(saleRepository.findByTotalPriceGreaterThan(preco)).thenReturn(List.of(sale));

        List<Sale> result = saleService.findByPriceGreaterThan(preco);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalPrice()).isGreaterThan(preco);
        verify(saleRepository, times(1)).findByTotalPriceGreaterThan(preco);
    }

    @Test
    @DisplayName("findByPriceLessThan - deve retornar vendas com preço menor que o informado")
    void findByPriceLessThan_deveRetornarVendas() {
        BigDecimal preco = new BigDecimal("200.00");
        when(saleRepository.findByTotalPriceLessThan(preco)).thenReturn(List.of(sale));

        List<Sale> result = saleService.findByPriceLessThan(preco);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalPrice()).isLessThan(preco);
        verify(saleRepository, times(1)).findByTotalPriceLessThan(preco);
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update - deve atualizar e retornar a venda quando encontrada")
    void update_deveAtualizarVendaQuandoEncontrada() {
        Sale detalhes = new Sale();
        detalhes.setTotalPrice(new BigDecimal("200.00"));
        detalhes.setDiscount(new BigDecimal("20.00"));
        detalhes.setPaymentMethod(PaymentMethodEnum.DEBITCARD);

        Sale atualizada = new Sale();
        atualizada.setId(1L);
        atualizada.setTotalPrice(new BigDecimal("200.00"));
        atualizada.setDiscount(new BigDecimal("20.00"));
        atualizada.setPaymentMethod(PaymentMethodEnum.DEBITCARD);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenReturn(atualizada);

        Sale result = saleService.update(1L, detalhes);

        assertThat(result.getTotalPrice()).isEqualByComparingTo("200.00");
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethodEnum.DEBITCARD);
        verify(saleRepository, times(1)).findById(1L);
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando venda não encontrada")
    void update_deveLancarExcecaoQuandoNaoEncontrada() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.update(99L, new Sale()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Venda não encontrada");

        verify(saleRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteById / existsById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(saleRepository).deleteById(1L);

        saleService.deleteById(1L);

        verify(saleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar true quando venda existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(saleRepository.existsById(1L)).thenReturn(true);

        assertThat(saleService.existsById(1L)).isTrue();
        verify(saleRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando venda não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        when(saleRepository.existsById(99L)).thenReturn(false);

        assertThat(saleService.existsById(99L)).isFalse();
        verify(saleRepository, times(1)).existsById(99L);
    }
}
