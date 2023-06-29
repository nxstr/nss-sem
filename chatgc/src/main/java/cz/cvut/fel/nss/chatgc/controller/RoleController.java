package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.controller.users.LoginController;
import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.dto.RoleDto;
import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@AllArgsConstructor
public class RoleController {
    private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);
    @Autowired
    private final RoleService roleService;

    @PostMapping(value = "/api/role/new")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> createRole(@RequestBody RoleDto dto){
        try {
            roleService.createRoleFromDto(dto);
            LOG.info("Role {} successfully created", dto.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException e){
            LOG.info(e.getMessage() + ": {}", dto.getName());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value="/api/role/edit")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> updateRole(@RequestBody RoleDto dto){
        try{
            if(dto.getName().equals("admin")){
                throw new RoleException("admin role is not allowed to modify");
            }
            roleService.changeRoleFromDto(dto);
            LOG.info("Role {}({}) successfully updated", dto.getName(), dto.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException e){
            LOG.info(e.getMessage() + ": {}({})", dto.getName(), dto.getId());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/api/role/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> deleteRole(@PathVariable Integer id){
        Role role = roleService.findById(id).orElse(null);
        if(role!=null) {
            try {
                if(role.getName().equals("admin")){
                    throw new RoleException("admin role is not allowed to modify");
                }
                roleService.delete(role);
                LOG.info("Role {}({}) successfully deleted", role.getName(), role.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (RoleException e) {
                LOG.info(e.getMessage() + ": {}({})", role.getName(), role.getId());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else{
            LOG.info("role does not exist: {}", id);
            return new ResponseEntity<>("role does not exist", HttpStatus.BAD_REQUEST);
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
