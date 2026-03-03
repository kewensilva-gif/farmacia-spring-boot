package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.entities.User;
import com.kewen.GerenciamentoFarmacia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    public List<User> findEnabled() {
        return userRepository.findByEnabledTrue();
    }

    public List<User> findDisabled() {
        return userRepository.findByEnabledFalse();
    }

    public User update(UUID id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setEnabled(userDetails.getEnabled());
            user.setRoles(userDetails.getRoles());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
