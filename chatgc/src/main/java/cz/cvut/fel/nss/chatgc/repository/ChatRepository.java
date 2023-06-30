package cz.cvut.fel.nss.chatgc.repository;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    /**
     * Searching for chat by playerUsername name.
     *
     * @param playerUsername represents name of chat owner (player)
     * @return Chat
     */

    public Chat findByPlayerUsername(String playerUsername);

    /**
     * Searching for chats that contain category.
     *
     * @param category represents category, that has chat
     * @return ArrayList<Chat>
     */

    public ArrayList<Chat> findChatsByCategories(Category category);
}
