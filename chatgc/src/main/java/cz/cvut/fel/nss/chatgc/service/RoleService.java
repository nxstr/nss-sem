package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.dto.RoleDto;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.repository.RoleRepository;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final CategoryService categoryService;
    private final ApplicationEventPublisher publisher;
    private final EmployeeService employeeService;

    @Transactional
    public Role persist(Role role){
        if(findByName(role.getName())!=null){
            throw new RoleException("role with this name exists");
        }
        roleRepository.save(role);
        if(role.getParentRole()!=null){
            Set<Category> cats = role.getCategories();
            for(Category r: role.getParentRole().getCategories()){
                cats.add(r);
            }
            role.setCategories(cats);

            role.getParentRole().getChildrenRoles().add(role);
            update(role.getParentRole());
        }
        update(role);
        Role role1 = findByName(role.getName());
        System.out.println("on persist cats: " + role1.getCategories().size());
        return role;
        //publisher
    }

    @Transactional
    public void createRoleFromDto(RoleDto dto){
        Role parent = null;
        Set<Category> cats = new HashSet<>();
        for (CategoryDto categoryDto : dto.getCategoryDtoList()) {
            Integer id = categoryDto.getId();
            if (categoryService.findById(id) != null) {
                cats.add(categoryService.findById(id));
            }
        }
        if (dto.getParentId() != null) {
            parent = findById(dto.getParentId()).orElse(null);
        }
        Role role = new Role(dto.getName(), cats, parent, new HashSet<>());
        role = persist(role);
        System.out.println("on dto create cats: " + role.getCategories().size());
    }

    public List<Employee> findAllEmployeesByRoleId(Integer id){
        List<Employee> res = new ArrayList<>();
        List<Employee> all = employeeService.findAllEmployees();
        Role role = findById(id).orElse(null);
        if(role!=null && !all.isEmpty()) {
            for (Employee e : all) {
                if (e.getRole().equals(role)) {
                    res.add(e);
                }
            }
        }
        return res;
    }

    @Transactional
    public Role update(Role role){
        roleRepository.save(role);
        for(Employee e: findAllEmployeesByRoleId(role.getId())){
            e.setRole(role);
            employeeService.update(e);
        }
        return role;
    }

    @Transactional
    public void delete(Role role){
        if(!findAllEmployeesByRoleId(role.getId()).isEmpty()){
            throw new RoleException("role is used by employee(s)");
        }
        for(Role r: role.getChildrenRoles()){
            removeChild(r);
        }
        removeRoleParent(role);
        roleRepository.delete(role);
    }

    public Role findByName(String name){
        return roleRepository.findByName(name);
    }

    public Optional<Role> findById(Integer id){
        return roleRepository.findById(id);
    }

    public List<Role> findAllRolesByCategoryId(Integer id){
        List<Role> roles = new ArrayList<>();
        for(Role r: findAll()){
            if(r.getCategories().stream().map(Category::getId).toList().contains(id)){
                roles.add(r);
            }
        }
        return roles;
    }

    @Transactional
    public List<Role> changeCategoryInAllRoles(Category category){
        List<Role> roles = findAllRolesByCategoryId(category.getId());
        if(roles!=null){
            Category cat = roles.get(0).getCategories().stream().filter(d-> d.getId().equals(category.getId())).findAny().orElse(null);
            if(cat!=null){
                for(Role r: roles){
                    removeRoleCategory(r, cat);
                }

                Set<Category> cats = new HashSet<>();
                cats.add(category);
                for(Role r: roles){
                    addRoleCategories(r, cats);
                }
            }
        }
        return roles;
    }

    @Transactional
    public List<Role> deleteCategoryInAllRoles(Category category){
        List<Role> roles = findAllRolesByCategoryId(category.getId());
        for(Role r: roles){
            removeRoleCategory(r, category);
        }
        return roles;
    }

    @Transactional
    public void updateRoleName(Role role, String name){
        if(findByName(name)!=null){
            throw new RoleException("role with this name exists");
        }
        if(role.getParentRole()!=null){
            for(Role r: role.getParentRole().getChildrenRoles()){
                if(r.equals(role)){
                    r.setName(name);
                }
            }
            update(role.getParentRole());
        }
        if(!role.getChildrenRoles().isEmpty()){
            for(Role c: role.getChildrenRoles()){
                c.getParentRole().setName(name);
                update(c);
                System.out.println("on update parent name : " + c.getParentRole().getName());
            }
        }
        role.setName(name);
        update(role);
    }

    @Transactional
    public void removeRoleParent(Role role){
        if(role.getParentRole()!=null) {
            Role parent = role.getParentRole();

            removeParentRoleCategories(role, role.getParentRole().getCategories());
            role.setParentRole(null);
            update(role);

            Set<Role> roles = new HashSet<>(parent.getChildrenRoles());
            roles.remove(role);
            parent.setChildrenRoles(roles);
            parent = update(parent);
        }
    }

    @Transactional
    public void addRoleParent(Role role, Role newParent){
        if(role.getParentRole()==null){
            role.setParentRole(newParent);

            addRoleCategories(role, newParent.getCategories());

            update(role);

            newParent.getChildrenRoles().add(role);
            update(newParent);
        }
    }

    @Transactional
    public void changeRoleParent(Role role, Role newParent){
        removeRoleParent(role);
        addRoleParent(role, newParent);
    }

    @Transactional
    public void addRoleCategories(Role role, Set<Category> categories){
        role.getCategories().addAll(categories);
        if(role.getChildrenRoles().isEmpty()){
            return;
        }
        for(Role c: role.getChildrenRoles()){
            addRoleCategories(c, categories);
        }
        update(role);
    }

    @Transactional
    public void removeParentRoleCategories(Role role, Set<Category> categories){
        role.getCategories().removeAll(categories);
        if(role.getChildrenRoles().isEmpty()){
            return;
        }
        for(Role c: role.getChildrenRoles()){
            removeParentRoleCategories(c, categories);
        }
        update(role);
    }

    @Transactional
    public void removeRoleCategory(Role role, Category category){
        if(role.getParentRole()==null || !role.getParentRole().getCategories().contains(category)){
            role.getCategories().remove(category);
            for(Role c: role.getChildrenRoles()){
                Set<Category> cats = new HashSet<>();
                cats.add(category);
                removeParentRoleCategories(c, cats);
            }
            role = update(role);
        }
    }

    @Transactional
    public void addChild(Role role, Role child){
        if(child.getParentRole()==null) {
            addRoleParent(child, role);
        }
    }

    @Transactional
    public void removeChild(Role child){
        removeRoleParent(child);
    }

    @Transactional
    public void changeRoleFromDto(RoleDto dto){
        Role roleNotUpdated = findById(dto.getId()).orElse(null);
        Role parent = null;
        if (dto.getParentId() != null) {
            parent = findById(dto.getParentId()).orElse(null);
        }
        Set<Category> cats = new HashSet<>();
        for (CategoryDto categoryDto : dto.getCategoryDtoList()) {
            Integer id = categoryDto.getId();
            if (categoryService.findById(id) != null) {
                cats.add(categoryService.findById(id));
            }
        }
        assert roleNotUpdated != null;
        if(roleNotUpdated.getParentRole()!=null) {
            if (!roleNotUpdated.getParentRole().equals(parent)) {
                changeRoleParent(roleNotUpdated, parent);
            }
        }

        if(!cats.equals(roleNotUpdated.getCategories())){
            Set<Category> forRemove = new HashSet<>();
            for(Category c: roleNotUpdated.getCategories()){
                if(!cats.contains(c)){
                   forRemove.add(c);
                }
            }
            for(Category c: forRemove){
                removeRoleCategory(roleNotUpdated, c);
            }
        }
        if(!cats.equals(roleNotUpdated.getCategories())){
            Set<Category> forAdd = new HashSet<>();
            for(Category c: cats){
                if(!roleNotUpdated.getCategories().contains(c)){
                    forAdd.add(c);
                }
            }
            addRoleCategories(roleNotUpdated, forAdd);
        }

        if(!roleNotUpdated.getName().equals(dto.getName())){
            updateRoleName(roleNotUpdated, dto.getName());
        }


    }

    public List<Role> findAll(){
        return roleRepository.findAll();
    }
}
