package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.dto.EmployeeDTO;
import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeServiceImpl employeeService;
    private final RoleService roleService;
    @Autowired
    private Visitor v;


    /**
     * Creates new employee from employeeDto.
     *
     * @param dto EmployeeDTO that contains employee data
     * @return ResponseEntity<String>
     */
    @PostMapping("/api/employee/new")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeDTO dto) {
        try {
            Employee e = dto.accept(v);
            employeeService.create(e);
            LOG.info("Employee {} successfully created", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RoleException | ExistsException | AccountException e) {
            LOG.info(e.getMessage() + ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Finds data of employee by id.
     *
     * @param id Integer id of employee
     * @return EmployeeDTO
     */
    @GetMapping("/api/employee/get/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public EmployeeDTO getEmployee(@PathVariable Integer id) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            return new EmployeeDTO();
        }
        return employee.accept(v);
    }

    /**
     * Finds all employees.
     *
     * @return List<EmployeeDTO>
     */
    @GetMapping("/api/employee/getAll")
    @PreAuthorize("hasAuthority('admin')")
    public List<EmployeeDTO> getEmployees() {
        List<Employee> employees = employeeService.findAllEmployees();
        List<EmployeeDTO> emps = new ArrayList<>();
        for (Employee e : employees) {
            emps.add(e.accept(v));
        }
        return emps;
    }

    /**
     * Updates existing employee by id.
     *
     * @param dto EmployeeDTO contains new data, that have to save
     * @param id  Integer id of employee
     * @return ResponseEntity<String>
     */
    @PutMapping("/api/employee/update/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> updateEmployee(@RequestBody EmployeeDTO dto, @PathVariable Integer id) {
        try {
            Employee e = employeeService.findById(id);
            if (!e.getRole().getId().equals(dto.getRoleId())) {
                roleService.findById(dto.getRoleId()).ifPresent(role -> employeeService.changeRole(e, role));
            }
            employeeService.updateEmployeeFromDto(dto, id);
            LOG.info("Employee {} successfully updated", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RoleException | ExistsException | AccountException e) {
            LOG.info(e.getMessage() + ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes employee by id.
     *
     * @param id Integer id of employee, that will be deleted
     * @return ResponseEntity<String>
     */
    @DeleteMapping("/api/emaployee/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id) {
        try {
            Employee e = employeeService.findById(id);
            employeeService.delete(e);
            LOG.info("Employee {} successfully deleted", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException e) {
            LOG.info(e.getMessage() + ": {}", id);
            return new ResponseEntity<>("employee does not exist", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets current authorized employee's data.
     *
     * @param principal Principal
     * @return EmployeeDTO
     */
    @GetMapping(value = "/api/employee/current")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public EmployeeDTO getCurrent(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        Employee acc = employeeService.findById(id);
        return acc.accept(v);
    }

    /**
     * Edits current authorized employee's data.
     *
     * @param principal Principal
     * @param dto       EmployeeDTO contains new data, that have to save
     * @return ResponseEntity<String>
     */
    @PutMapping(value = "api/employee/current/edit")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<String> editCurrent(Principal principal, @RequestBody EmployeeDTO dto) {
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            employeeService.updateEmployeeFromDto(dto, id);
            LOG.info("Employee {} successfully updated", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RoleException | ExistsException | AccountException e) {
            LOG.info(e.getMessage() + ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creates admin account if there are no registered admins in system database.
     *
     * @param dto EmployeeDTO represents data, that will be saved
     * @return ResponseEntity<String>
     */
    @PostMapping("api/register/emp")
    public ResponseEntity<String> registerAdminOnSystemInitialization(@RequestBody EmployeeDTO dto) {
        if (employeeService.findAllEmployees().stream().filter(d -> d.getRole().getName().equals("admin")).toList().isEmpty()) {
            if (Objects.isNull(roleService.findByName("admin"))) {
                roleService.initializeAdminRole();
            }
            Role role = roleService.findByName("admin");
            Employee employee = new Employee(dto.getUsername(), dto.getEmail(), dto.getPassword(), role);
            employeeService.create(employee);
            LOG.info("Employee {} successfully created", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOG.info("admin account already exist");
            return new ResponseEntity<>("admin account already exist", HttpStatus.BAD_REQUEST);
        }
    }
}
