package com.kewen.GerenciamentoFarmacia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public record EmployeeDto(
    String firstname,
    String lastname,
    String cpf,
    LocalDate hiringDate,
    Optional<LocalDate> terminationDate,
    LocalDate birthDate,
    BigDecimal salary,
    String roleName
) {}
