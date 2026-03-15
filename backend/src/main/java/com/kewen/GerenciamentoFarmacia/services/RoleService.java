package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Role;
import com.kewen.GerenciamentoFarmacia.repositories.RoleRepository;
import com.kewen.GerenciamentoFarmacia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public Role save(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RuntimeException("Já existe uma Role com nome '" + role.getName() + "'");
        }

        return roleRepository.save(role);
    }

    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public Role update(UUID id, Role roleDetails) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role não encontrada"));

        validateName(roleDetails.getName(), existingRole);

        existingRole.setName(roleDetails.getName());

        return roleRepository.save(existingRole);
    }

    private void validateName(String newName, Role existingRole) {
        if (newName == null) {
            throw new IllegalArgumentException("Nome da role é obrigatório");
        }

        boolean nameChanged = !newName.equals(existingRole.getName());

        if (nameChanged && roleRepository.existsByName(newName)) {
            throw new IllegalArgumentException("Já existe uma Role com nome '" + newName + "'");
        }
    }

    public void deleteById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role não encontrada"));

        if (userRepository.existsByRoleUuid(id)) {
            throw new IllegalStateException("Role não pode ser excluída pois está vinculada a usuários");
        }

        roleRepository.delete(role);
    }

    public boolean existsById(UUID id) {
        return roleRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}
