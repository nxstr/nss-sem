package cz.cvut.fel.nss.chatgc.service.impl.users;

import cz.cvut.fel.nss.chatgc.dto.PlayerDto;
import cz.cvut.fel.nss.chatgc.events.PlayerEvent;
import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.User;
import cz.cvut.fel.nss.chatgc.repository.users.PlayerRepository;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents User Service.
 */

@Service
public class PlayerServiceImpl extends UserServiceImpl<Player> {

    private final PlayerRepository playerDao;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder encoder;
    private final ChatService chatService;

    @Autowired
    private Visitor v;

    public PlayerServiceImpl(UserRepository<Player, Integer> userDao, ApplicationEventPublisher publisher, PlayerRepository playerDao, PasswordEncoder encoder, ChatService chatService) {
        super(userDao, publisher, encoder);
        this.playerDao = playerDao;
        this.publisher = publisher;
        this.encoder = encoder;
        this.chatService = chatService;
    }

    /**
     * Validates data and creates new player.
     *
     * @param player Player that will be saved
     */
    @Transactional
    public void create(Player player) {
        if (this.findByUsername(player.getUsername()) != null) {
            throw new ExistsException("username already exists");
        }
        if (this.findByEmail(player.getEmail()) != null) {
            throw new ExistsException("email already exists");
        }
        if (player.getPassword().equals("") || player.getPassword() == null) {
            throw new AccountException("password can not be empty");
        }
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (!patternMatches(player.getEmail(), regexPattern)) {
            throw new ExistsException("email is not valid");
        }
        publisher.publishEvent(new PlayerEvent("create", new PlayerDto(player.getUsername(), player.getEmail(), player.getPassword(), null)));
        this.persist(player);
    }

    /**
     * Finds player by its id.
     *
     * @param id Integer id of player
     * @return Player
     */
    @Transactional
    public Player findById(Integer id) {
        return (Player) playerDao.findById(id).orElse(null);
    }

    /**
     * Changes user's username.
     *
     * @param player  Player that will be updated
     * @param newName String new username of player
     */
    @Transactional
    public void changeUsername(Player player, String newName) {
        if (findByUsername(newName) != null) {
            throw new ExistsException("username already exists");
        }
        publisher.publishEvent(new PlayerEvent("updateData", player.accept(v)));
        player.setUsername(newName);
        Chat chat = player.getChat();
        chat.setPlayerUsername(newName);
        chat.setPlayer(player);
        chatService.update(chat);
        update(player);
        publisher.publishEvent(new PlayerEvent("updateUsername", player.accept(v)));
    }

    /**
     * Finds all players.
     *
     * @return List<Player>
     */
    public List<Player> findAllPlayers() {
        List<Player> players = new ArrayList<>();
        for (User e : playerDao.findAll()) {
            if (Objects.equals(e.getClass().getAnnotation(DiscriminatorValue.class).value(), "PLAYER")) {
                Player player = (Player) e;
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Validates data and updates player.
     *
     * @param dto PlayerDTO entity that contains data for validation
     * @param id  Integer id of player that will be updated
     */
    @Transactional
    public void updatePlayer(PlayerDto dto, Integer id) {
        Player player = findById(id);
        Objects.requireNonNull(player);
        if (!dto.getEmail().equals(player.getEmail())) {
            changeEmail(player, dto.getEmail());
        }
        if (!dto.getPassword().equals("") && !encoder.matches(dto.getPassword(), player.getPassword())) {
            publisher.publishEvent(new PlayerEvent("updatePass", dto));
            changePassword(player, dto.getPassword());
        }
        if (!dto.getUsername().equals(player.getUsername())) {
            changeUsername(player, dto.getUsername());
        }

    }

}
