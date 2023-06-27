package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.dto.RoleDto;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@AllArgsConstructor
public class RoleController {
    @Autowired
    private final RoleService roleService;

    @PostMapping(value = "/api/role/new")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity createRole(@RequestBody RoleDto dto){
        try {
            roleService.createRoleFromDto(dto);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value="/api/role/edit")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity updateRole(@RequestBody RoleDto dto){
        try{
            if(dto.getName().equals("admin")){
                throw new RoleException("admin role is not allowed to modify");
            }
            roleService.changeRoleFromDto(dto);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(value = "/api/role/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity deleteRole(@PathVariable Integer id){
        Role role = roleService.findById(id).orElse(null);
        if(role!=null) {
            if(role.getName().equals("admin")){
                throw new RoleException("admin role is not allowed to modify");
            }
            try {
                roleService.delete(role);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (RoleException e) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value ="/api/roles")
    @PreAuthorize("hasAuthority('admin')")
    public List<RoleDto> getRoleList(){
        List<RoleDto> roles = new ArrayList<>();
        for(Role r: roleService.findAll()){
            RoleDto dto = new RoleDto();
            dto.setName(r.getName());
            dto.setId(r.getId());
            dto.setParentId(null);
            if(r.getParentRole()!=null) {
                dto.setParentId(r.getParentRole().getId());
            }
            List<CategoryDto> cats = new ArrayList<>();
            for(Category c: r.getCategories()){
                cats.add(new CategoryDto(c.getId(), c.getName()));
            }
            dto.setCategoryDtoList(cats);
            System.out.println("on get roles cats: " + r.getCategories().size());
            roles.add(dto);
        }
        return roles;
    }

    @GetMapping(value = "/api/roles/get/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public RoleDto getRole(@PathVariable Integer id){
        RoleDto dto = new RoleDto();
        Role role = roleService.findById(id).orElse(null);
        if(role!=null){
            dto.setId(role.getId());
            dto.setName(role.getName());
            if(role.getParentRole()!=null) {
                dto.setParentId(role.getParentRole().getId());
            }
            List<CategoryDto> cats = new ArrayList<>();
            for(Category c: role.getCategories()){
                cats.add(new CategoryDto(c.getId(), c.getName()));
            }
            dto.setCategoryDtoList(cats);
        }
        return dto;
    }
}
