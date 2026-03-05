package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Product;
import com.kewen.GerenciamentoFarmacia.entities.Sale;
import com.kewen.GerenciamentoFarmacia.entities.SaleProduct;
import com.kewen.GerenciamentoFarmacia.enums.PaymentMethodEnum;
import com.kewen.GerenciamentoFarmacia.repositories.SaleProductRepository;

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
class SaleProductServiceTest {

    @Mock
    private SaleProductRepository saleProductRepository;

    @InjectMocks
    private SaleProductService saleProductService;

    private SaleProduct saleProduct;
    private Sale sale;
    private Product product;

    @BeforeEach
    void setUp() {
        sale = new Sale();
        sale.setId(1L);
        sale.setTotalPrice(new BigDecimal("25.00"));
        sale.setDiscount(new BigDecimal("0.00"));
        sale.setPaymentMethod(PaymentMethodEnum.PIX);

        product = new Product();
        product.setId(1L);
        product.setName("Paracetamol 500mg");
        product.setBarcode("7891234560001");
        product.setUnitPrice(new BigDecimal("12.50"));

        saleProduct = new SaleProduct();
        saleProduct.setId(1L);
        saleProduct.setSale(sale);
        saleProduct.setProduct(product);
        saleProduct.setQuantity(2L);
        saleProduct.setUnitPrice(new BigDecimal("12.50"));
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save - deve salvar e retornar o item da venda")
    void save_deveSalvarERetornarSaleProduct() {
        when(saleProductRepository.save(any(SaleProduct.class))).thenReturn(saleProduct);

        SaleProduct result = saleProductService.save(saleProduct);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getQuantity()).isEqualTo(2L);
        verify(saleProductRepository, times(1)).save(saleProduct);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById - deve retornar Optional com item quando encontrado")
    void findById_deveRetornarItemQuandoEncontrado() {
        when(saleProductRepository.findById(1L)).thenReturn(Optional.of(saleProduct));

        Optional<SaleProduct> result = saleProductService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getProduct().getName()).isEqualTo("Paracetamol 500mg");
        verify(saleProductRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrado")
    void findById_deveRetornarVazioQuandoNaoEncontrado() {
        when(saleProductRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<SaleProduct> result = saleProductService.findById(99L);

        assertThat(result).isEmpty();
        verify(saleProductRepository, times(1)).findById(99L);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll - deve retornar lista de itens da venda")
    void findAll_deveRetornarListaDeItens() {
        when(saleProductRepository.findAll()).thenReturn(List.of(saleProduct));

        List<SaleProduct> result = saleProductService.findAll();

        assertThat(result).hasSize(1);
        verify(saleProductRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // findBySaleId / findByProductId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findBySaleId - deve retornar itens da venda pelo saleId")
    void findBySaleId_deveRetornarItensPorVenda() {
        when(saleProductRepository.findBySaleId(1L)).thenReturn(List.of(saleProduct));

        List<SaleProduct> result = saleProductService.findBySaleId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSale().getId()).isEqualTo(1L);
        verify(saleProductRepository, times(1)).findBySaleId(1L);
    }

    @Test
    @DisplayName("findByProductId - deve retornar itens da venda pelo productId")
    void findByProductId_deveRetornarItensPorProduto() {
        when(saleProductRepository.findByProductId(1L)).thenReturn(List.of(saleProduct));

        List<SaleProduct> result = saleProductService.findByProductId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProduct().getId()).isEqualTo(1L);
        verify(saleProductRepository, times(1)).findByProductId(1L);
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update - deve atualizar e retornar o item quando encontrado")
    void update_deveAtualizarItemQuandoEncontrado() {
        SaleProduct detalhes = new SaleProduct();
        detalhes.setSale(sale);
        detalhes.setProduct(product);
        detalhes.setQuantity(5L);
        detalhes.setUnitPrice(new BigDecimal("12.50"));

        SaleProduct atualizado = new SaleProduct();
        atualizado.setId(1L);
        atualizado.setQuantity(5L);
        atualizado.setUnitPrice(new BigDecimal("12.50"));

        when(saleProductRepository.findById(1L)).thenReturn(Optional.of(saleProduct));
        when(saleProductRepository.save(any(SaleProduct.class))).thenReturn(atualizado);

        SaleProduct result = saleProductService.update(1L, detalhes);

        assertThat(result.getQuantity()).isEqualTo(5L);
        verify(saleProductRepository, times(1)).findById(1L);
        verify(saleProductRepository, times(1)).save(any(SaleProduct.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando item não encontrado")
    void update_deveLancarExcecaoQuandoNaoEncontrado() {
        when(saleProductRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleProductService.update(99L, new SaleProduct()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Produto da venda não encontrado");

        verify(saleProductRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteById / existsById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(saleProductRepository).deleteById(1L);

        saleProductService.deleteById(1L);

        verify(saleProductRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar true quando item existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(saleProductRepository.existsById(1L)).thenReturn(true);

        assertThat(saleProductService.existsById(1L)).isTrue();
        verify(saleProductRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando item não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        when(saleProductRepository.existsById(99L)).thenReturn(false);

        assertThat(saleProductService.existsById(99L)).isFalse();
        verify(saleProductRepository, times(1)).existsById(99L);
    }
}
