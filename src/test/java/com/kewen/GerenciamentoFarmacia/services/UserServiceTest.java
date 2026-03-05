package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.User;
import com.kewen.GerenciamentoFarmacia.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setUuid(userId);
        user.setUsername("pedroalves");
        user.setEmail("pedro@email.com");
        user.setPassword("senha789");
        user.setEnabled(true);
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save - deve salvar e retornar o usuário")
    void save_deveSalvarERetornarUsuario() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("pedroalves");
        assertThat(result.getEmail()).isEqualTo("pedro@email.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("save - deve lançar IllegalAccessError quando username ou email já existe")
    void save_deveLancarExcecaoQuandoViolacaoDeIntegridade() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(IllegalAccessError.class)
                .hasMessage("Username ou email já existe");

        verify(userRepository, times(1)).save(user);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById - deve retornar Optional com usuário quando encontrado")
    void findById_deveRetornarUsuarioQuandoEncontrado() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("pedroalves");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrado")
    void findById_deveRetornarVazioQuandoNaoEncontrado() {
        UUID outroId = UUID.randomUUID();
        when(userRepository.findById(outroId)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(outroId);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(outroId);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll - deve retornar lista de usuários")
    void findAll_deveRetornarListaDeUsuarios() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // findByUsername / findByEmail / findByUsernameOrEmail
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByUsername - deve retornar usuário pelo username")
    void findByUsername_deveRetornarUsuarioPeloUsername() {
        when(userRepository.findByUsername("pedroalves")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("pedroalves");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("pedro@email.com");
        verify(userRepository, times(1)).findByUsername("pedroalves");
    }

    @Test
    @DisplayName("findByEmail - deve retornar usuário pelo email")
    void findByEmail_deveRetornarUsuarioPeloEmail() {
        when(userRepository.findByEmail("pedro@email.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("pedro@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("pedroalves");
        verify(userRepository, times(1)).findByEmail("pedro@email.com");
    }

    @Test
    @DisplayName("findByUsernameOrEmail - deve retornar usuário quando username ou email batem")
    void findByUsernameOrEmail_deveRetornarUsuario() {
        when(userRepository.findByUsernameOrEmail("pedroalves", "pedro@email.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsernameOrEmail("pedroalves", "pedro@email.com");

        assertThat(result).isPresent();
        verify(userRepository, times(1)).findByUsernameOrEmail("pedroalves", "pedro@email.com");
    }

    // -------------------------------------------------------------------------
    // findEnabled / findDisabled
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findEnabled - deve retornar usuários habilitados")
    void findEnabled_deveRetornarUsuariosHabilitados() {
        when(userRepository.findByEnabledTrue()).thenReturn(List.of(user));

        List<User> result = userService.findEnabled();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEnabled()).isTrue();
        verify(userRepository, times(1)).findByEnabledTrue();
    }

    @Test
    @DisplayName("findDisabled - deve retornar usuários desabilitados")
    void findDisabled_deveRetornarUsuariosDesabilitados() {
        user.setEnabled(false);
        when(userRepository.findByEnabledFalse()).thenReturn(List.of(user));

        List<User> result = userService.findDisabled();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEnabled()).isFalse();
        verify(userRepository, times(1)).findByEnabledFalse();
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update - deve atualizar campos não-nulos e retornar o usuário")
    void update_deveAtualizarUsuarioQuandoEncontrado() {
        User detalhes = new User();
        detalhes.setUsername("pedroalves");
        detalhes.setEmail("pedro@email.com");
        detalhes.setPassword("novaSenha");
        detalhes.setEnabled(true);

        User atualizado = new User();
        atualizado.setUuid(userId);
        atualizado.setUsername("pedroalves");
        atualizado.setEmail("pedro@email.com");
        atualizado.setPassword("novaSenha");
        atualizado.setEnabled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(atualizado);

        User result = userService.update(userId, detalhes);

        assertThat(result.getPassword()).isEqualTo("novaSenha");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando usuário não encontrado")
    void update_deveLancarExcecaoQuandoNaoEncontrado() {
        UUID outroId = UUID.randomUUID();
        when(userRepository.findById(outroId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(outroId, new User()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("update - deve lançar IllegalAccessError quando novo username já está em uso")
    void update_deveLancarExcecaoQuandoUsernameJaExiste() {
        User detalhes = new User();
        detalhes.setUsername("outroUsuario");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("outroUsuario")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(userId, detalhes))
                .isInstanceOf(IllegalAccessError.class)
                .hasMessage("Username já existe");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("update - deve lançar IllegalAccessError quando novo email já está em uso")
    void update_deveLancarExcecaoQuandoEmailJaExiste() {
        User detalhes = new User();
        detalhes.setEmail("outro@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(userId, detalhes))
                .isInstanceOf(IllegalAccessError.class)
                .hasMessage("Email já existe");

        verify(userRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    // -------------------------------------------------------------------------
    // existsById / existsByUsername / existsByEmail
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("existsById - deve retornar true quando usuário existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThat(userService.existsById(userId)).isTrue();
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando usuário não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        UUID outroId = UUID.randomUUID();
        when(userRepository.existsById(outroId)).thenReturn(false);

        assertThat(userService.existsById(outroId)).isFalse();
        verify(userRepository, times(1)).existsById(outroId);
    }

    @Test
    @DisplayName("existsByUsername - deve retornar true quando username existe")
    void existsByUsername_deveRetornarTrueQuandoUsernameExiste() {
        when(userRepository.existsByUsername("pedroalves")).thenReturn(true);

        assertThat(userService.existsByUsername("pedroalves")).isTrue();
        verify(userRepository, times(1)).existsByUsername("pedroalves");
    }

    @Test
    @DisplayName("existsByEmail - deve retornar true quando email existe")
    void existsByEmail_deveRetornarTrueQuandoEmailExiste() {
        when(userRepository.existsByEmail("pedro@email.com")).thenReturn(true);

        assertThat(userService.existsByEmail("pedro@email.com")).isTrue();
        verify(userRepository, times(1)).existsByEmail("pedro@email.com");
    }
}
