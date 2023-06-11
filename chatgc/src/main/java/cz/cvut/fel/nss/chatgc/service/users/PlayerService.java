package cz.cvut.fel.nss.chatgc.service.users;

import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.PlayerRoles;
import cz.cvut.fel.nss.chatgc.repository.users.PlayerRepository;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class PlayerService extends UserService<Player>{

    private final PlayerRepository playerDao;

    public PlayerService(UserRepository<Player, Integer> userDao, ApplicationEventPublisher publisher, PlayerRepository playerDao, PasswordEncoder encoder) {
        super(userDao, publisher, encoder);
        this.playerDao = playerDao;
    }


    @Transactional
    public Player findById(Integer id){
        return (Player) playerDao.findById(id).orElse(null);
    }



}
