package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.controller.CategoryController;
import cz.cvut.fel.nss.chatgc.dto.PlayerDto;
import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.PlayerRoles;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.impl.users.PlayerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlayerController {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerServiceImpl playerService;

    @PostMapping("api/register/player")
    public ResponseEntity<String> registerPlayer(@RequestBody HashMap<String, String> request){
        try {
            Player player = new Player();
            player.setUsername(request.get("username"));
            player.setPassword(request.get("password"));
            player.setEmail(request.get("email"));
            player.setRole(PlayerRoles.REGISTERED);
            playerService.create(player);
            LOG.info("Player {} successfully created", request.get("username"));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (ExistsException | AccountException e){
            LOG.info(e.getMessage(), ": {}", request.get("username"));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "api/player/current")
    @PreAuthorize("hasAuthority('PLAYER')")
    public PlayerDto getCurrentPlayer(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        Player acc = playerService.findById(id);
        PlayerDto dto = new PlayerDto(acc.getUsername(), acc.getEmail(), "", id);
        return dto;
    }


    @PutMapping(value = "api/player/current/edit")
    @PreAuthorize("hasAuthority('PLAYER')")
    public ResponseEntity<String> editCurrent(Principal principal, @RequestBody PlayerDto dto){
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            playerService.updatePlayer(dto, id);
            LOG.info("Player {} successfully updated", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (ExistsException | AccountException e){
            LOG.info(e.getMessage(), ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "api/player/{id}/edit")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> updatePlayer(@PathVariable Integer id, @RequestBody PlayerDto dto){
        try {
            playerService.updatePlayer(dto, id);
            LOG.info("Player {} successfully updated", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (ExistsException | AccountException e){
            LOG.info(e.getMessage(), ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "api/player/all")
    @PreAuthorize("hasAuthority('admin')")
    public List<PlayerDto> getPlayers(){
        List<Player> players = playerService.findAllPlayers();
        List<PlayerDto> dtoList = new ArrayList<>();
        for(Player p: players){
            PlayerDto dto = new PlayerDto(p.getUsername(), p.getEmail(), "", p.getId());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @GetMapping(value = "api/player/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public PlayerDto getPlayer(@PathVariable Integer id){
        Player player = playerService.findById(id);
        try{
            return new PlayerDto(player.getUsername(), player.getEmail(), "", id);
        }catch (NullPointerException e){
            return new PlayerDto();
        }
    }


}
