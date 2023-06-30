package cz.cvut.fel.nss.chatgc.mapper;

import cz.cvut.fel.nss.chatgc.dto.*;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.Player;

/**
 * Mapper for several entities. Visitor design pattern.
 */
public interface Visitor {

    /**
     * Converts Category to CategoryDto.
     *
     * @param category entity for conversion
     * @return CategoryDto
     */
    CategoryDto visitCategoryEntity(Category category);

    /**
     * Converts CategoryDto to Category.
     *
     * @param categoryDto dto for conversion
     * @return Category
     */
    Category visitCategoryDto(CategoryDto categoryDto);

    /**
     * Converts Employee to EmployeeDTO.
     *
     * @param employee entity for conversion
     * @return EmployeeDTO
     */
    EmployeeDTO visitEmployeeEntity(Employee employee);

    /**
     * Converts EmployeeDto to Employee.
     *
     * @param employeeDTO dto for conversion
     * @return Employee
     */
    Employee visitEmployeeDto(EmployeeDTO employeeDTO);

    /**
     * Converts Player to PlayerDto.
     *
     * @param player entity for conversion
     * @return PlayerDto
     */
    PlayerDto visitPlayerEntity(Player player);

    /**
     * Converts PlayerDto to Player.
     *
     * @param playerDto dto for conversion
     * @return Player
     */
    Player visitPlayerDto(PlayerDto playerDto);

    /**
     * Converts Role to RoleDto.
     *
     * @param role entity for conversion
     * @return RoleDto
     */
    RoleDto visitRoleEntity(Role role);

    /**
     * Converts RoleDto to Role.
     *
     * @param roleDto dto for conversion
     * @return Role
     */
    Role visitRoleDto(RoleDto roleDto);
}
