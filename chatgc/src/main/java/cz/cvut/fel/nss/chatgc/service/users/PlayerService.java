package cz.cvut.fel.nss.chatgc.service.users;

import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.PlayerRoles;
import cz.cvut.fel.nss.chatgc.repository.users.PlayerRepository;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class PlayerService extends UserService<Player>{

    private final PlayerRepository playerDao;
//    private final UserService chatService;
    public static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;

    public PlayerService(UserRepository<Player, Integer> userDao, ApplicationEventPublisher publisher, PlayerRepository playerDao) {
        super(userDao, publisher);
        this.playerDao = playerDao;
    }




    @Transactional
    public Player findById(Integer id){
        return (Player) playerDao.findById(id).orElse(null);
    }

    public SseEmitter registerClient(String name) {
        var emitter = new SseEmitter(DEFAULT_TIMEOUT);
        var client = new Client(emitter, name);

        if(playerDao.findByUsername(name)==null){
            persist(new Player(name, "testEmail", "testPass", PlayerRoles.REGISTERED));
        }

        //move to event handler maybe

        addOnlineUsers(client);
        sendWelcomeToClient(client);

        System.out.println("New client registeres");
        return emitter;
    }


}
