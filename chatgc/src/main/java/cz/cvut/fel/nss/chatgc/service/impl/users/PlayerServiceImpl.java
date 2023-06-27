package cz.cvut.fel.nss.chatgc.service.impl.users;

import cz.cvut.fel.nss.chatgc.dto.PlayerDto;
import cz.cvut.fel.nss.chatgc.events.PlayerEvent;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.repository.users.PlayerRepository;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PlayerServiceImpl extends UserServiceImpl<Player> {

    private final PlayerRepository playerDao;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder encoder;
    private final ChatService chatService;

    public PlayerServiceImpl(UserRepository<Player, Integer> userDao, ApplicationEventPublisher publisher, PlayerRepository playerDao, PasswordEncoder encoder, ChatService chatService) {
        super(userDao, publisher, encoder);
        this.playerDao = playerDao;
        this.publisher = publisher;
        this.encoder = encoder;
        this.chatService = chatService;
    }

    @Transactional
    public void create(Player player){
        if(this.findByUsername(player.getUsername())!=null){
            System.out.println("here");
            throw new ExistsException("username already exists");
        }
        if(this.findByEmail(player.getEmail())!=null){
            throw new ExistsException("email already exists");
        }
//        publisher.publishEvent(new PlayerEvent("create", new PlayerDto(player.getUsername(), player.getEmail(), player.getPassword(), null)));
        this.persist(player);
    }


    @Transactional
    public Player findById(Integer id){
        return (Player) playerDao.findById(id).orElse(null);
    }

    //change username (attention to chat, generate event that updates only name in last message from user, no need for chatListUpdate)
    @Transactional
    public void changeUsername(Player player, String newName){
        if(findByUsername(newName)!=null){
            throw new ExistsException("username already exists");
        }
        publisher.publishEvent(new PlayerEvent("updateData", new PlayerDto(player.getUsername(), player.getEmail(), "", player.getId())));
        player.setUsername(newName);
        Chat chat = player.getChat();
        chat.setPlayerUsername(newName);
        chat.setPlayer(player);
        chatService.update(chat);
        update(player);
        publisher.publishEvent(new PlayerEvent("updateUsername", new PlayerDto(player.getUsername(), player.getEmail(), "", player.getId())));
    }

    @Transactional
    public void updatePlayer(PlayerDto dto, Integer id){
        Player player = findById(id);
        Objects.requireNonNull(player);
        if(!dto.getUsername().equals(player.getUsername())){
            changeUsername(player, dto.getUsername());
        }
        if(!dto.getEmail().equals(player.getEmail())){
            changeEmail(player, dto.getEmail());
        }
        if(!dto.getPassword().equals("") && !encoder.matches(dto.getPassword(), player.getPassword())){
            publisher.publishEvent(new PlayerEvent("updatePass", dto));
            changePassword(player, dto.getPassword());
        }
    }

}
