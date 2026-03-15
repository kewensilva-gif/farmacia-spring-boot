package com.kewen.GerenciamentoFarmacia.dto;

import java.time.LocalDate;

public record CustomerDto(
    String firstname,
    String lastname,
    String cpf,
    LocalDate registrationDate,
    String roleName
) {}
