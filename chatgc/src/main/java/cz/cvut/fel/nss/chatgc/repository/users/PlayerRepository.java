package cz.cvut.fel.nss.chatgc.repository.users;

import cz.cvut.fel.nss.chatgc.model.users.Player;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends UserRepository<Player, Integer>{
}
