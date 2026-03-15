package com.kewen.GerenciamentoFarmacia.mappers;

import com.kewen.GerenciamentoFarmacia.dto.EmployeeDto;
import com.kewen.GerenciamentoFarmacia.entities.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "person.firstname", target = "firstname")
    @Mapping(source = "person.lastname", target = "lastname")
    @Mapping(source = "person.cpf", target = "cpf")
    @Mapping(source = "person.user.role.name", target = "roleName")
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "terminationDate", expression = "java(java.util.Optional.ofNullable(employee.getTerminationDate()))")
    EmployeeDto toEmployeeDto(Employee employee);

    List<EmployeeDto> toEmployeeDtoList(List<Employee> employees);
}
