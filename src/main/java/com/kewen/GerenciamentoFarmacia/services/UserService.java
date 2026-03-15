package com.kewen.GerenciamentoFarmacia.services;

import com.kewen.GerenciamentoFarmacia.dto.UserDto;
import com.kewen.GerenciamentoFarmacia.entities.User;
import com.kewen.GerenciamentoFarmacia.mappers.UserMapper;
import com.kewen.GerenciamentoFarmacia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Username ou email já existe");
        }
    }

    public Optional<UserDto> findById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper::toUserDto);
    }

    public List<UserDto> findAll() {
        return userRepository.findAllUsers();
    }

    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toUserDto);
    }

    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toUserDto);
    }

    public Optional<UserDto> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).map(userMapper::toUserDto);
    }

    public List<UserDto> findEnabled() {
        return userRepository.findAllEnabledUsers();
    }

    public List<UserDto> findDisabled() {
        return userRepository.findAllDisabledUsers();
    }

    public User update(UUID id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        validateUsername(userDetails.getUsername(), existingUser);
        validateEmail(userDetails.getEmail(), existingUser);

        updateFields(existingUser, userDetails);

        return userRepository.save(existingUser);
    }

    private void validateUsername(String newUsername, User existingUser) {
        if (newUsername == null) return;

        boolean usernameChanged = !newUsername.equals(existingUser.getUsername());

        if (usernameChanged && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("Username já existe");
        }
    }

    private void validateEmail(String newEmail, User existingUser) {
        if (newEmail == null) return;

        boolean emailChanged = !newEmail.equals(existingUser.getEmail());

        if (emailChanged && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email já existe");
        }
    }

    private void updateFields(User existingUser, User userDetails) {
        if (userDetails.getUsername() != null) {
            existingUser.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getEnabled() != null) {
            existingUser.setEnabled(userDetails.getEnabled());
        }

        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }
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
