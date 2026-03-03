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
        return roleRepository.findById(id).map(role -> {
            role.setName(roleDetails.getName());
            return roleRepository.save(role);
        }).orElseThrow(() -> new RuntimeException("Role não encontrada"));
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
