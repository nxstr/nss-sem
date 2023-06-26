package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.dto.EmployeeDTO;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final RoleService roleService;

    @PostMapping("/api/employee/new")
    @PreAuthorize("hasAuthority('admin')")
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
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/api/employee/get/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public EmployeeDTO getEmployee(@PathVariable Integer id){
        Employee employee = employeeService.findById(id);
        EmployeeDTO dto = new EmployeeDTO();
        if(employee!=null){
            dto.setUsername(employee.getUsername());
            dto.setEmail(employee.getEmail());
            dto.setRoleId(employee.getRole().getId());
            dto.setRoleName(employee.getRole().getName());
            dto.setId(employee.getId());
        }
        return dto;
    }

    @GetMapping("/api/employee/getAll")
    @PreAuthorize("hasAuthority('admin')")
    public List<EmployeeDTO> getEmployees(){
        List<Employee> employees = employeeService.findAllEmployees();
        List<EmployeeDTO> emps = new ArrayList<>();
        for(Employee e: employees){
            EmployeeDTO dto = new EmployeeDTO();
            dto.setUsername(e.getUsername());
            dto.setEmail(e.getEmail());
            dto.setRoleId(e.getRole().getId());
            dto.setRoleName(e.getRole().getName());
            dto.setId(e.getId());
            emps.add(dto);
        }
        return emps;
    }

    @PutMapping("/api/employee/update/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity updateEmployee(@RequestBody EmployeeDTO dto, @PathVariable Integer id){
        try {
            Employee e = employeeService.findById(id);
            if (!e.getRole().getId().equals(dto.getRoleId())) {
                System.out.println("role");
                Role role = roleService.findById(dto.getRoleId()).orElse(null);
                if (role != null) {
                    employeeService.changeRole(e, role);
                }
            }
            employeeService.updateEmployeeFromDto(dto, id);
            //update role if not updateFromDto
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException | ExistsException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/api/emaployee/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity deleteEmployee(@PathVariable Integer id){
        Employee e = employeeService.findById(id);
        employeeService.delete(e);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/api/employee/current")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public EmployeeDTO getCurrent(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        Employee acc = employeeService.findById(id);
        EmployeeDTO dto = new EmployeeDTO(acc.getUsername(), "", acc.getEmail(), acc.getRole().getId(), acc.getRole().getName(), acc.getId());
        return dto;
    }

    @PutMapping(value = "api/employee/current/edit")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity editCurrent(Principal principal, @RequestBody EmployeeDTO dto){
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            employeeService.updateEmployeeFromDto(dto, id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException | ExistsException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("api/reg/emp")
    public void regEmp(){
        Role role = roleService.findByName("admin");
        Employee employee = new Employee("testEmp", "ira111kirilenko@gmail.com", "test", role);
        employeeService.create(employee);
    }






}
