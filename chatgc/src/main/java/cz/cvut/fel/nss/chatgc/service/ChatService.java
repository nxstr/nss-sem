package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.repository.ChatRepository;
import cz.cvut.fel.nss.chatgc.service.messages.RequestService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import cz.cvut.fel.nss.chatgc.service.users.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final PlayerService playerService;
    private final RequestService requestService;
    private final ApplicationEventPublisher publisher;
    private final EmployeeService employeeService;


    @Transactional
    public void persist(Chat chat){
        chatRepository.save(chat);
    }

    public List<Chat> findAll(){
        return chatRepository.findAll();
    }

    public List<Chat> findAllByCategoryId(Integer id){
        List<Chat> chats = new ArrayList<>();
        for(Chat c: findAll()){
            if(c.getCategories().stream().map(Category::getId).toList().contains(id)){
                chats.add(c);
            }
        }
        return chats;
    }

    @Transactional
    public Chat findById(Integer id){
        return chatRepository.findById(id).orElse(null);
    }

    public Chat findByPlayer(String player){
        Chat chat = chatRepository.findByPlayerUsername(player);
        System.out.println(chat);
        return chat;
    }

    @Transactional
    public Chat update(Chat chat){
        Chat res = chatRepository.save(chat);
        return res;
    }
}
