package com.kewen.GerenciamentoFarmacia.mappers;

import com.kewen.GerenciamentoFarmacia.dto.CustomerDto;
import com.kewen.GerenciamentoFarmacia.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "person.firstname", target = "firstname")
    @Mapping(source = "person.lastname", target = "lastname")
    @Mapping(source = "person.cpf", target = "cpf")
    @Mapping(source = "person.user.role.name", target = "roleName")
    CustomerDto toCustomerDto(Customer customer);

    List<CustomerDto> toCustomerDtoList(List<Customer> customers);
}
