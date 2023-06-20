package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.dto.EmployeeDTO;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final RoleService roleService;

    @PostMapping("/api/employee/new")
    public ResponseEntity createEmployee(@RequestBody EmployeeDTO dto){
        try{
            Role role = roleService.findById(dto.getRoleId()).orElse(null);
            if(role==null){
                throw new RoleException("role not found");
            }
            Employee e = new Employee(dto.getUsername(), dto.getEmail(), dto.getPassword(), role);
            e.setResponses(new ArrayList<>());
            employeeService.create(e);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException | ExistsException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/api/employee/get/{id}")
    public EmployeeDTO getEmployee(@PathVariable Integer id){
        Employee employee = employeeService.findById(id);
        EmployeeDTO dto = new EmployeeDTO();
        if(employee!=null){
            dto.setUsername(employee.getUsername());
            dto.setEmail(employee.getEmail());
            dto.setRoleId(employee.getRole().getId());
        }
        return dto;
    }

//    public void updateEmployee(@RequestBody EmployeeDTO dto, @PathVariable Integer id){
//        if(!employee.getRole().getId().equals(dto.getRoleId())){
//            changeRole(employee, );
//        }
//        //update role if not updateFromDto
//    }





}
