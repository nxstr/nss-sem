package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final RoleService roleService;

    @PostMapping("/api/reg")
    public void createEmployee(){

            Employee e = (Employee) employeeService.findByUsername("testEmp");
            for(Response r: e.getResponses()){
                r.setEmployee(null);
            }

        Employee employee = new Employee();
        employee.setUsername("testEmp");
        employee.setPassword("test");
        employee.setEmail("testemail");
        employee.setRole(roleService.findByName("admin"));
        employee.setResponses(new ArrayList<>());
        employeeService.persist(employee);
    }



}
