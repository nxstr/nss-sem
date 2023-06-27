package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.dto.RoleDto;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    Role persist(Role role);
    void createRoleFromDto(RoleDto dto);
    List<Employee> findAllEmployeesByRoleId(Integer id);
    Role update(Role role);
    void delete(Role role);
    Role findByName(String name);
    Optional<Role> findById(Integer id);
    List<Role> findAllRolesByCategoryId(Integer id);
    List<Role> changeCategoryInAllRoles(Category category);
    List<Role> deleteCategoryInAllRoles(Category category);
    void updateRoleName(Role role, String name);
    void removeRoleParent(Role role);
    void addRoleParent(Role role, Role newParent);
    void changeRoleParent(Role role, Role newParent);
    void addRoleCategories(Role role, Set<Category> categories);
    void removeParentRoleCategories(Role role, Set<Category> categories);
    void removeRoleCategory(Role role, Category category);
    void addChild(Role role, Role child);
    void removeChild(Role child);
    void changeRoleFromDto(RoleDto dto);
    List<Role> findAll();
    void initializeAdminRole();
}
