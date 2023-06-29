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
     * @param role Role entity that will be saved
     * @return Role
     */
    Role persist(Role role);

    /**
     * Finds all employees that have this role.
     * @param id Integer id of role
     * @return List<Employee>
     */
    List<Employee> findAllEmployeesByRoleId(Integer id);

    /**
     * Updates role.
     * @param role Role entity that will be updated
     * @return Role
     */
    Role update(Role role);

    /**
     * Deletes role.
     * @param role Role entity that will be deleted
     */
    void delete(Role role);

    /**
     * Finds role by its name.
     * @param name String name of role
     * @return Role
     */
    Role findByName(String name);

    /**
     * Finds role by its id.
     * @param id Integer id of role
     * @return Optional<Role>
     */
    Optional<Role> findById(Integer id);

    /**
     * Finds all roles that contain category.
     * @param id Integer id of category
     * @return List<Role>
     */
    List<Role> findAllRolesByCategoryId(Integer id);

    /**
     * IF category has been updated, it will update all roles that contain changed category.
     * @param category Category changed
     * @return List<Role>
     */
    List<Role> changeCategoryInAllRoles(Category category);

    /**
     * If category will be deleted, it will delete this category from all roles that contain it.
     * @param category Category that will be deleted
     * @return List<Role>
     */
    List<Role> deleteCategoryInAllRoles(Category category);

    /**
     * Updates role name.
     * @param role Role that will be updated
     * @param name String new role name
     */
    void updateRoleName(Role role, String name);

    /**
     * Sets parentRole of role to null.
     * @param role Role with parent
     */
    void removeRoleParent(Role role);

    /**
     * If parentRole of role is null, it will set newParent to role.
     * @param role Role without a parentRole
     * @param newParent Role will be a new parentRole of role
     */
    void addRoleParent(Role role, Role newParent);

    /**
     * Changes one parentRole to another.
     * @param role Role that will change its old parentRole
     * @param newParent Role new parentRole for role
     */
    void changeRoleParent(Role role, Role newParent);

    /**
     * Adds these categories to role and its children.
     * @param role Role that will get new categories
     * @param categories Set<Category> will be added to role
     */
    void addRoleCategories(Role role, Set<Category> categories);

    /**
     * Removes these categories from role and its children.
     * @param role Role that will be modified
     * @param categories Set<Category> will be removed from role
     */
    void removeParentRoleCategories(Role role, Set<Category> categories);

    /**
     * Removes one category from role.
     * @param role Role that will be modified
     * @param category Category that will be removed from role
     */
    void removeRoleCategory(Role role, Category category);

    /**
     * Adds new role to childrenRoles set.
     * @param role Role parent role
     * @param child Role new child role
     */
    void addChild(Role role, Role child);

    /**
     * Removes one child from childrenRoles set.
     * @param child Role child that will be removed
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
