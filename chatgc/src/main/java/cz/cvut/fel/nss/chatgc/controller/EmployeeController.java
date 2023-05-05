package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/register-employee/{name}")
    public SseEmitter sseEmitter(@PathVariable String name) {
        return employeeService.registerClient(name);
    }
}
