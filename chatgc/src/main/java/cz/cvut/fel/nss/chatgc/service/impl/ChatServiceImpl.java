package cz.cvut.fel.nss.chatgc.service.impl;

import cz.cvut.fel.nss.chatgc.events.ChatEvent;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.repository.ChatRepository;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ApplicationEventPublisher publisher;


    @Transactional
    public void persist(Chat chat) {
        chatRepository.save(chat);
    }

    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    public List<Chat> findAllByCategoryId(Integer id) {
        List<Chat> chats = new ArrayList<>();
        for (Chat c : findAll()) {
            if (c.getCategories().stream().map(Category::getId).toList().contains(id)) {
                chats.add(c);
            }
        }
        return chats;
    }

    @Transactional
    public Chat findById(Integer id) {
        return chatRepository.findById(id).orElse(null);
    }

    public Chat findByPlayer(String player) {
        return chatRepository.findByPlayerUsername(player);
    }

    @Transactional
    public Chat update(Chat chat) {
        return chatRepository.save(chat);
    }

    @Transactional
    public void setChatClose(Chat chat) {
        if (!chat.isOpen()) {
            throw new ExistsException("chat is already closed");
        }
        chat.setOpen(false);
        chat.setCategories(new HashSet<>());
        update(chat);
        publisher.publishEvent(new ChatEvent("update", chat));
    }

    @Transactional
    public void setChatOpen(Chat chat) {
        if (chat.isOpen()) {
            throw new ExistsException("chat is already open");
        }
        chat.setOpen(true);
        update(chat);
        publisher.publishEvent(new ChatEvent("update", chat));
    }
}
