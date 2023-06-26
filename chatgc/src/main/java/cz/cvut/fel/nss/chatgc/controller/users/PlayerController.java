package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.PlayerRoles;
import cz.cvut.fel.nss.chatgc.service.users.PlayerService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;


    @GetMapping(value = "/api/current_user")
    public Object getCurrentUser() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    @PostMapping("/api/reg/player")
    public void createEmployee(){
        Player employee = new Player();
        employee.setUsername("testPl");
        employee.setPassword("test");
        employee.setEmail("testemailPl");
        employee.setRole(PlayerRoles.REGISTERED);
        playerService.persist(employee);
    }

    @PostMapping("api/register/player")
    public ResponseEntity registerPlayer(@RequestBody HashMap<String, String> request){
        Player player = new Player();
        player.setUsername(request.get("username"));
        player.setPassword(request.get("password"));
        player.setEmail(request.get("email"));
        player.setRole(PlayerRoles.REGISTERED);
        playerService.persist(player);
        return new ResponseEntity(HttpStatus.OK);
    }



}
