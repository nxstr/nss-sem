package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.dto.RoleDto;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);
    @Autowired
    private final RoleService roleService;
    @Autowired
    private Visitor v;

    /**
     * Creates new role.
     * @param dto RoleDto has data that will be saved
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "/api/role/new")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> createRole(@RequestBody RoleDto dto) {
        try {
            Role role = dto.accept(v);
            roleService.persist(role);
            LOG.info("Role {} successfully created", dto.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RoleException e) {
            LOG.info(e.getMessage() + ": {}", dto.getName());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates role by id.
     * @param dto RoleDto has data that will be saved
     * @return ResponseEntity<String>
     */
    @PutMapping(value = "/api/role/edit")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> updateRole(@RequestBody RoleDto dto) {
        try {
            if (dto.getName().equals("admin")) {
                throw new RoleException("admin role is not allowed to modify");
            }
            roleService.changeRoleFromDto(dto);
            LOG.info("Role {}({}) successfully updated", dto.getName(), dto.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RoleException e) {
            LOG.info(e.getMessage() + ": {}({})", dto.getName(), dto.getId());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes role by id.
     * @param id Integer id of role
     * @return ResponseEntity<String>
     */
    @DeleteMapping(value = "/api/role/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> deleteRole(@PathVariable Integer id) {
        Role role = roleService.findById(id).orElse(null);
        if (role != null) {
            try {
                if (role.getName().equals("admin")) {
                    throw new RoleException("admin role is not allowed to modify");
                }
                roleService.delete(role);
                LOG.info("Role {}({}) successfully deleted", role.getName(), role.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (RoleException e) {
                LOG.info(e.getMessage() + ": {}({})", role.getName(), role.getId());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            LOG.info("role does not exist: {}", id);
            return new ResponseEntity<>("role does not exist", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Finds all roles.
     * @return List<RoleDto>
     */
    @GetMapping(value = "/api/roles")
    @PreAuthorize("hasAuthority('admin')")
    public List<RoleDto> getRoleList() {
        List<RoleDto> roles = new ArrayList<>();
        for (Role r : roleService.findAll()) {
            roles.add(r.accept(v));
        }
        return roles;
    }

    /**
     * Finds role's data by id
     * @param id Integer id of role
     * @return RoleDto
     */
    @GetMapping(value = "/api/roles/get/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public RoleDto getRole(@PathVariable Integer id) {
        RoleDto dto = new RoleDto();
        Role role = roleService.findById(id).orElse(null);
        if (role != null) {
            dto = role.accept(v);
        }
        return dto;
    }
}
