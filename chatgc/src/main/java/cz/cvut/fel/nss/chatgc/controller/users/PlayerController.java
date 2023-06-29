package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.dto.PlayerDto;
import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.PlayerRoles;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.impl.users.PlayerServiceImpl;
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
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlayerController {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerServiceImpl playerService;
    @Autowired
    private Visitor v;

    /**
     * Creates new player account.
     * @param dto HashMap<String, String> represents username, email and raw password.
     * @return ResponseEntity<String>
     */
    @PostMapping("api/register/player")
    public ResponseEntity<String> registerPlayer(@RequestBody PlayerDto dto){
        try {
            playerService.create(dto.accept(v));
            LOG.info("Player {} successfully created", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (ExistsException | AccountException e){
            LOG.info(e.getMessage() + ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets current authorized player's data.
     * @param principal Principal
     * @return PlayerDto
     */
    @GetMapping(value = "api/player/current")
    @PreAuthorize("hasAuthority('PLAYER')")
    public PlayerDto getCurrentPlayer(Principal principal) {
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        Player acc = playerService.findById(id);
        return acc.accept(v);
    }


    /**
     * Edits current authorized player's data.
     * @param principal Principal
     * @param dto PlayerDto contains data, that will be saved
     * @return ResponseEntity<String>
     */
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
            LOG.info(e.getMessage() + ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates player by id.
     * @param id Integer id of player.
     * @param dto PlayerDto contains data, that will be saved
     * @return ResponseEntity<String>
     */
    @PutMapping(value = "api/player/{id}/edit")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> updatePlayer(@PathVariable Integer id, @RequestBody PlayerDto dto){
        try {
            playerService.updatePlayer(dto, id);
            LOG.info("Player {} successfully updated", dto.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (ExistsException | AccountException e){
            LOG.info(e.getMessage() + ": {}", dto.getUsername());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Finds all players.
     * @return List<PlayerDto>
     */
    @GetMapping(value = "api/player/all")
    @PreAuthorize("hasAuthority('admin')")
    public List<PlayerDto> getPlayers(){
        List<Player> players = playerService.findAllPlayers();
        List<PlayerDto> dtoList = new ArrayList<>();
        for(Player p: players){
            dtoList.add(p.accept(v));
        }
        return dtoList;
    }

    /**
     * Finds player;s data by id.
     * @param id Integer id of player.
     * @return PlayerDto
     */
    @GetMapping(value = "api/player/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public PlayerDto getPlayer(@PathVariable Integer id){
        Player player = playerService.findById(id);
        try{
            return player.accept(v);
        }catch (NullPointerException e){
            return new PlayerDto();
        }
    }
}
