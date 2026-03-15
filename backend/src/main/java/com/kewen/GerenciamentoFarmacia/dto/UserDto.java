package com.kewen.GerenciamentoFarmacia.dto;

public record UserDto(
    String username,
    String email,
    Boolean enabled,
    String roleName
) {}
