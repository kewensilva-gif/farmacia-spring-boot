package com.kewen.GerenciamentoFarmacia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
    @NotBlank @Size(max = 30) String firstName,
    @NotBlank @Size(max = 60) String lastName,
    @NotBlank @Size(min = 11, max = 11) String cpf,
    @NotBlank @Size(max = 30) String username,
    @NotBlank @Email @Size(max = 50) String email,
    @NotBlank @Size(min = 6, max = 100) String password,
    @NotBlank @Pattern(regexp = "ADMIN|EMPLOYEE|CUSTOMER", message = "Role deve ser ADMIN, EMPLOYEE ou CUSTOMER") String roleName,
    LocalDate registrationDate,
    LocalDate hiringDate,
    BigDecimal salary
) {}