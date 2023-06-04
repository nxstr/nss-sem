package cz.cvut.fel.nss.chatgc.repository;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    public Chat findByPlayerUsername(String playerUsername);

    public ArrayList<Chat> findChatsByCategories(Category category);
}
