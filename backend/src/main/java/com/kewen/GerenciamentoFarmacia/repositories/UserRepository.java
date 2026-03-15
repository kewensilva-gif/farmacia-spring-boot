package com.kewen.GerenciamentoFarmacia.repositories;

import com.kewen.GerenciamentoFarmacia.dto.UserDto;
import com.kewen.GerenciamentoFarmacia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByRoleUuid(UUID roleId);
    List<User> findByEnabled(Boolean enabled);
    List<User> findByEnabledTrue();
    List<User> findByEnabledFalse();
    
    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    Optional<User> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    @Query("""
           SELECT new com.kewen.GerenciamentoFarmacia.dto.UserDto(
                u.username,
                u.email,
                u.enabled,
                u.role.name
           )
           FROM User u
           """)
    List<UserDto> findAllUsers();

    @Query("""
           SELECT new com.kewen.GerenciamentoFarmacia.dto.UserDto(
                u.username,
                u.email,
                u.enabled,
                u.role.name
           )
           FROM User u WHERE u.enabled = true
           """)
    List<UserDto> findAllEnabledUsers();

    @Query("""
           SELECT new com.kewen.GerenciamentoFarmacia.dto.UserDto(
                u.username,
                u.email,
                u.enabled,
                u.role.name
           )
           FROM User u WHERE u.enabled = false
           """)
    List<UserDto> findAllDisabledUsers();
}