package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final CategoryService categoryService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Role persist(Role role){
        if(role.getParentRole()!=null){
            role.getParentRole().getChildrenRoles().add(role);
            update(role.getParentRole());

            role.getCategories().addAll(role.getParentRole().getCategories());
        }
        roleRepository.save(role);
        return role;
        //publisher
    }

    @Transactional
    public Role update(Role role){
        roleRepository.save(role);
        return role;
    }

    public Role findByName(String name){
        return roleRepository.findByName(name);
    }

    public Optional<Role> findById(Integer id){
        return roleRepository.findById(id);
    }

    @Transactional
    public void updateRoleName(Role role, String name){
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
            }
        }
        role.setName(name);
        update(role);
    }

    @Transactional
    public void removeRoleParent(Role role){
        if(role.getParentRole()!=null) {
            Set<Role> roles = new HashSet<>(role.getParentRole().getChildrenRoles());
            roles.remove(role);
            role.getParentRole().setChildrenRoles(roles);
            Role parent = update(role.getParentRole());

            removeParentRoleCategories(role, parent.getCategories());
            role.setParentRole(null);
            update(role);
        }
    }

    @Transactional
    public void addRoleParent(Role role, Role newParent){
        if(role.getParentRole()==null){
            role.setParentRole(newParent);

            newParent.getChildrenRoles().add(role);
            update(newParent);

            addRoleCategories(role, newParent.getCategories());

            update(role);
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
            role = update(role);

            for(Role c: role.getChildrenRoles()){
                Set<Category> cats = new HashSet<>();
                cats.add(category);
                removeParentRoleCategories(c, cats);
            }
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
}
