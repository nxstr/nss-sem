package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.messages.Message;

public interface MessageService {

    Message persist(Message message);

    void update(Message message);

    Message findById(Integer id);

    void delete(Message message);
}
