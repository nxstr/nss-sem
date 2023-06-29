package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.Chat;

import java.util.List;

/**
 * Represents Chat Service.
 */

public interface ChatService {

    /**
     * Creates new chat.
     * @param chat entity that will be saved
     */
    void persist(Chat chat);

    /**
     * Finds all chats.
     * @return List<Chat>
     */
    List<Chat> findAll();

    /**
     * Finds all chats that contain category.
     * @param id category id
     * @return List<Chat>
     */
    List<Chat> findAllByCategoryId(Integer id);

    /**
     * Finds chat by its id.
     * @param id chat id
     * @return Chat
     */
    Chat findById(Integer id);

    /**
     * Finds chat by name of chat owner (player).
     * @param player username of player
     * @return Chat
     */
    Chat findByPlayer(String player);

    /**
     * Updates chat.
     * @param chat entity that will be updated
     * @return Chat
     */
    Chat update(Chat chat);

    /**
     * Makes chat close. Clears all chat categories.
     * @param chat chat that will be closed
     */
    void setChatClose(Chat chat);

    /**
     * Makes chat open. Method is called when handles new Request message.
     * @param chat chat that will be opened
     */
    void setChatOpen(Chat chat);
}
