package com.kewen.GerenciamentoFarmacia.repositories;

import com.kewen.GerenciamentoFarmacia.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);

    // Queries para soft delete
    List<Category> findByEnabledTrue();
    Optional<Category> findByIdAndEnabledTrue(Long id);
    Optional<Category> findByNameAndEnabledTrue(String name);
    boolean existsByNameAndEnabledTrue(String name);
}
