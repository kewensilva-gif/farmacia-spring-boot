package com.kewen.GerenciamentoFarmacia.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.kewen.GerenciamentoFarmacia.dto.UserDto;
import com.kewen.GerenciamentoFarmacia.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "role.name", target = "roleName")
    UserDto toUserDto(User user);
    User userDTOToUser(UserDto userDto);
}
