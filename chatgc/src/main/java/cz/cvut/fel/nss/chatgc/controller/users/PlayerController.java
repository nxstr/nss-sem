package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.dto.PlayerDto;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.PlayerRoles;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.impl.users.PlayerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerServiceImpl playerService;


//    @GetMapping(value = "/api/current_user")
//    public Object getCurrentUser() {
//        return SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//    }
//
//    @PostMapping("/api/reg/player")
//    public void createEmployee(){
//        Player employee = new Player();
//        employee.setUsername("testPl");
//        employee.setPassword("test");
//        employee.setEmail("testemailPl");
//        employee.setRole(PlayerRoles.REGISTERED);
//        playerService.persist(employee);
//    }

    @PostMapping("api/register/player")
    public ResponseEntity registerPlayer(@RequestBody HashMap<String, String> request){
        Player player = new Player();
        player.setUsername(request.get("username"));
        player.setPassword(request.get("password"));
        player.setEmail(request.get("email"));
        player.setRole(PlayerRoles.REGISTERED);
        playerService.create(player);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "api/player/current")
    @PreAuthorize("hasAuthority('PLAYER')")
    public PlayerDto getCurrentPlayer(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        Player acc = playerService.findById(id);
        PlayerDto dto = new PlayerDto(acc.getUsername(), acc.getEmail(), "", id);
        System.out.println(dto);
        return dto;
    }


    @PutMapping(value = "api/player/current/edit")
    @PreAuthorize("hasAuthority('PLAYER')")
    public ResponseEntity editCurrent(Principal principal, @RequestBody PlayerDto dto){
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            playerService.updatePlayer(dto, id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException | ExistsException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "api/player/{id}/edit")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity updatePlayer(@PathVariable Integer id, @RequestBody PlayerDto dto){
        try {
            playerService.updatePlayer(dto, id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (RoleException | ExistsException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


}
