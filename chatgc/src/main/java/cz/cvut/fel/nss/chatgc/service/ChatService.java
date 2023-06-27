package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.Chat;

import java.util.List;

public interface ChatService {
    void persist(Chat chat);
    List<Chat> findAll();
    List<Chat> findAllByCategoryId(Integer id);
    Chat findById(Integer id);
    Chat findByPlayer(String player);
    Chat update(Chat chat);
    void setChatClose(Chat chat);
    void setChatOpen(Chat chat);
}
