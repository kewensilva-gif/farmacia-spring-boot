package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.Role;
import com.kewen.GerenciamentoFarmacia.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

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
                .orElseThrow(() -> new IllegalAccessError("Role não encontrada"));

        validateName(roleDetails.getName(), existingRole);

        existingRole.setName(roleDetails.getName());

        return roleRepository.save(existingRole);
    }

    private void validateName(String newName, Role existingRole) {
        if (newName == null) {
            throw new IllegalAccessError("Nome da role é obrigatório");
        }

        boolean nameChanged = !newName.equals(existingRole.getName());

        if (nameChanged && roleRepository.existsByName(newName)) {
            throw new IllegalAccessError("Já existe uma Role com nome '" + newName + "'");
        }
    }

    public void deleteById(UUID id) {
        roleRepository.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return roleRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
}
