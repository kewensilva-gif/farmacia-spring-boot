package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Category;
import com.kewen.GerenciamentoFarmacia.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Medicamentos");
    }

    @Test
    @DisplayName("save - deve salvar e retornar a categoria")
    void save_deveSalvarERetornarCategoria() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.save(category);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Medicamentos");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("findById - deve retornar Optional com categoria quando encontrada")
    void findById_deveRetornarCategoriaQuandoEncontrada() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Medicamentos");
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrada")
    void findById_deveRetornarVazioQuandoNaoEncontrada() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findById(99L);

        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("findAll - deve retornar lista de categorias")
    void findAll_deveRetornarListaDeCategorias() {
        Category outra = new Category();
        outra.setId(2L);
        outra.setName("Cosméticos");

        when(categoryRepository.findAll()).thenReturn(List.of(category, outra));

        List<Category> result = categoryService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Category::getName)
                .containsExactlyInAnyOrder("Medicamentos", "Cosméticos");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - deve retornar lista vazia quando não há categorias")
    void findAll_deveRetornarListaVaziaQuandoNaoHaCategorias() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<Category> result = categoryService.findAll();

        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findByName - deve retornar Optional com categoria quando encontrada pelo nome")
    void findByName_deveRetornarCategoriaQuandoEncontrada() {
        when(categoryRepository.findByName("Medicamentos")).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.findByName("Medicamentos");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(categoryRepository, times(1)).findByName("Medicamentos");
    }

    @Test
    @DisplayName("findByName - deve retornar Optional vazio quando nome não existe")
    void findByName_deveRetornarVazioQuandoNomeNaoExiste() {
        when(categoryRepository.findByName("Inexistente")).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findByName("Inexistente");

        assertThat(result).isEmpty();
        verify(categoryRepository, times(1)).findByName("Inexistente");
    }

    @Test
    @DisplayName("update - deve atualizar e retornar a categoria quando encontrada")
    void update_deveAtualizarCategoriaQuandoEncontrada() {
        Category detalhes = new Category();
        detalhes.setName("Vitaminas");

        Category categoriaAtualizada = new Category();
        categoriaAtualizada.setId(1L);
        categoriaAtualizada.setName("Vitaminas");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(categoriaAtualizada);

        Category result = categoryService.update(1L, detalhes);

        assertThat(result.getName()).isEqualTo("Vitaminas");
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando categoria não encontrada")
    void update_deveLancarExcecaoQuandoNaoEncontrada() {
        Category detalhes = new Category();
        detalhes.setName("Vitaminas");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(99L, detalhes))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrada");

        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteById(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar true quando categoria existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        boolean result = categoryService.existsById(1L);

        assertThat(result).isTrue();
        verify(categoryRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando categoria não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        boolean result = categoryService.existsById(99L);

        assertThat(result).isFalse();
        verify(categoryRepository, times(1)).existsById(99L);
    }
}
