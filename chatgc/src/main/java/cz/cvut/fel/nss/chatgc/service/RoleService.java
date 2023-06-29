package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.dto.RoleDto;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;

import java.util.List;
import java.util.Optional;
import java.util.Set;
/**
 * Represents Role Service.
 */
public interface RoleService {

    /**
     * Creates new role.
     * @param role entity that will be saved
     * @return Role
     */
    Role persist(Role role);

    /**
     * Validates data, converts to Role entity from RoleDto and calls persist.
     * @param dto RoleDto entity
     */
    void createRoleFromDto(RoleDto dto);

    /**
     * Finds all employees that have this role.
     * @param id Id of role
     * @return List<Employee>
     */
    List<Employee> findAllEmployeesByRoleId(Integer id);

    /**
     * Updates role.
     * @param role entity that will be updated
     * @return Role
     */
    Role update(Role role);

    /**
     * Deletes role.
     * @param role entity that will be deleted
     */
    void delete(Role role);

    /**
     * Finds role by its name.
     * @param name name of role
     * @return Role
     */
    Role findByName(String name);

    /**
     * Finds role by its id.
     * @param id Id of role
     * @return Optional<Role>
     */
    Optional<Role> findById(Integer id);

    /**
     * Finds all roles that contain category.
     * @param id Id of category
     * @return List<Role>
     */
    List<Role> findAllRolesByCategoryId(Integer id);

    /**
     * IF category has been updated, it will update all roles that contain changed category.
     * @param category changed category
     * @return List<Role>
     */
    List<Role> changeCategoryInAllRoles(Category category);

    /**
     * If category will be deleted, it will delete this category from all roles that contain it.
     * @param category category that will be deleted
     * @return List<Role>
     */
    List<Role> deleteCategoryInAllRoles(Category category);

    /**
     * Updates role name.
     * @param role Entity that will be updated
     * @param name new role name
     */
    void updateRoleName(Role role, String name);

    /**
     * Sets parentRole of role to null.
     * @param role role with parent
     */
    void removeRoleParent(Role role);

    /**
     * If parentRole of role is null, it will set newParent to role.
     * @param role role without a parentRole
     * @param newParent will be a new parentRole of role
     */
    void addRoleParent(Role role, Role newParent);

    /**
     * Changes one parentRole to another.
     * @param role role that will change its old parentRole
     * @param newParent new parentRole for role
     */
    void changeRoleParent(Role role, Role newParent);

    /**
     * Adds these categories to role and its children.
     * @param role entity that will get new categories
     * @param categories set of categories will be added to role
     */
    void addRoleCategories(Role role, Set<Category> categories);

    /**
     * Removes these categories from role and its children.
     * @param role entity that will be modified
     * @param categories set of categories will be removed from role
     */
    void removeParentRoleCategories(Role role, Set<Category> categories);

    /**
     * Removes one category from role.
     * @param role entity that will be modified
     * @param category entity that will be removed from role
     */
    void removeRoleCategory(Role role, Category category);

    /**
     * Adds new role to childrenRoles set.
     * @param role parent role
     * @param child new child role
     */
    void addChild(Role role, Role child);

    /**
     * Removes one child from childrenRoles set.
     * @param child child that will be removed
     */
    void removeChild(Role child);

    /**
     * Validates data from RoleDto and updates role.
     * @param dto RoleDto that has changed data
     */
    void changeRoleFromDto(RoleDto dto);

    /**
     * Finds all roles.
     * @return List<Role>
     */
    List<Role> findAll();

    /**
     * Initializes admin role if it does not exist.
     */
    void initializeAdminRole();
}
