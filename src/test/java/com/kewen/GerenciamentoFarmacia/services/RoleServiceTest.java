package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Role;
import com.kewen.GerenciamentoFarmacia.repositories.RoleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        role = new Role();
        role.setUuid(roleId);
        role.setName("ROLE_ADMIN");
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("save - deve salvar e retornar a role")
    void save_deveSalvarERetornarRole() {
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.save(role);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ROLE_ADMIN");
        verify(roleRepository, times(1)).save(role);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findById - deve retornar Optional com role quando encontrada")
    void findById_deveRetornarRoleQuandoEncontrada() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.findById(roleId);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ROLE_ADMIN");
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    @DisplayName("findById - deve retornar Optional vazio quando não encontrada")
    void findById_deveRetornarVazioQuandoNaoEncontrada() {
        UUID outroId = UUID.randomUUID();
        when(roleRepository.findById(outroId)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findById(outroId);

        assertThat(result).isEmpty();
        verify(roleRepository, times(1)).findById(outroId);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findAll - deve retornar lista de roles")
    void findAll_deveRetornarListaDeRoles() {
        Role outra = new Role();
        outra.setUuid(UUID.randomUUID());
        outra.setName("ROLE_USER");

        when(roleRepository.findAll()).thenReturn(List.of(role, outra));

        List<Role> result = roleService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Role::getName).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
        verify(roleRepository, times(1)).findAll();
    }

    // -------------------------------------------------------------------------
    // findByName
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("findByName - deve retornar role pelo nome")
    void findByName_deveRetornarRolePeloNome() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.findByName("ROLE_ADMIN");

        assertThat(result).isPresent();
        assertThat(result.get().getUuid()).isEqualTo(roleId);
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
    }

    @Test
    @DisplayName("findByName - deve retornar Optional vazio quando nome não existe")
    void findByName_deveRetornarVazioQuandoNaoExiste() {
        when(roleRepository.findByName("ROLE_INEXISTENTE")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findByName("ROLE_INEXISTENTE");

        assertThat(result).isEmpty();
        verify(roleRepository, times(1)).findByName("ROLE_INEXISTENTE");
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("update - deve atualizar e retornar a role quando encontrada")
    void update_deveAtualizarRoleQuandoEncontrada() {
        Role detalhes = new Role();
        detalhes.setName("ROLE_MANAGER");

        Role atualizada = new Role();
        atualizada.setUuid(roleId);
        atualizada.setName("ROLE_MANAGER");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(atualizada);

        Role result = roleService.update(roleId, detalhes);

        assertThat(result.getName()).isEqualTo("ROLE_MANAGER");
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("update - deve lançar RuntimeException quando role não encontrada")
    void update_deveLancarExcecaoQuandoNaoEncontrada() {
        UUID outroId = UUID.randomUUID();
        when(roleRepository.findById(outroId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.update(outroId, new Role()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Role não encontrada");

        verify(roleRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("deleteById - deve chamar deleteById no repositório")
    void deleteById_deveChamarDeleteById() {
        doNothing().when(roleRepository).deleteById(roleId);

        roleService.deleteById(roleId);

        verify(roleRepository, times(1)).deleteById(roleId);
    }

    // -------------------------------------------------------------------------
    // existsById / existsByName
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("existsById - deve retornar true quando role existe")
    void existsById_deveRetornarTrueQuandoExiste() {
        when(roleRepository.existsById(roleId)).thenReturn(true);

        assertThat(roleService.existsById(roleId)).isTrue();
        verify(roleRepository, times(1)).existsById(roleId);
    }

    @Test
    @DisplayName("existsById - deve retornar false quando role não existe")
    void existsById_deveRetornarFalseQuandoNaoExiste() {
        UUID outroId = UUID.randomUUID();
        when(roleRepository.existsById(outroId)).thenReturn(false);

        assertThat(roleService.existsById(outroId)).isFalse();
        verify(roleRepository, times(1)).existsById(outroId);
    }

    @Test
    @DisplayName("existsByName - deve retornar true quando nome existe")
    void existsByName_deveRetornarTrueQuandoNomeExiste() {
        when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(true);

        assertThat(roleService.existsByName("ROLE_ADMIN")).isTrue();
        verify(roleRepository, times(1)).existsByName("ROLE_ADMIN");
    }

    @Test
    @DisplayName("existsByName - deve retornar false quando nome não existe")
    void existsByName_deveRetornarFalseQuandoNomeNaoExiste() {
        when(roleRepository.existsByName("ROLE_INEXISTENTE")).thenReturn(false);

        assertThat(roleService.existsByName("ROLE_INEXISTENTE")).isFalse();
        verify(roleRepository, times(1)).existsByName("ROLE_INEXISTENTE");
    }
}
