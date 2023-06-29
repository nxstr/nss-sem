package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.messages.Message;

/**
 * Represents Message Service.
 */

public interface MessageService {

    /**
     * Creates new message.
     * @param message Message that will be saved
     * @return Message
     */
    Message persist(Message message);

    /**
     * Updates existing message.
     * @param message Message that will be updated
     */
    void update(Message message);

    /**
     * Finds message by its id.
     * @param id Integer id of message
     * @return Message
     */
    Message findById(Integer id);

    /**
     * Deletes message.
     * @param message Message that will be deleted
     */
    void delete(Message message);
}
