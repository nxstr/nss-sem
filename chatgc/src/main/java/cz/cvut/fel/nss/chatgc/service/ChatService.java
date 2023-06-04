package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.dto.ChatRequestDto;
import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.events.ChatServerEvent;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.messages.MessageType;
import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.model.users.User;
import cz.cvut.fel.nss.chatgc.repository.ChatRepository;
import cz.cvut.fel.nss.chatgc.service.messages.RequestService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import cz.cvut.fel.nss.chatgc.service.users.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    @Transactional
    public Chat findById(Integer id){
        return chatRepository.findById(id).orElse(null);
    }

    public Chat findByPlayer(String player){
        Chat chat = chatRepository.findByPlayerUsername(player);
        System.out.println(chat);
        return chat;
    }


    public void broadcast(ChatRequestDto dto) {
        System.out.println(dto);
        Set<Client> users1 = new HashSet<>();

        for(Client c: employeeService.getUsers()){
            User user = employeeService.findByUsername(c.getName());
            if(employeeService.findById(user.getId()).getRole().getName().equals("admin")){
                users1.add(c);
            }
        }

        for(Client c: playerService.getUsers()){
            if(c.getName().equals(dto.chatName())){
                users1.add(c);
            }
        }
//
//        Set<User> users = new HashSet<>();
//        for(Client c:users1){
//            users.add(userService.findByUsername(c.getName()));
//        }

        User user = playerService.findByUsername(dto.chatName());

        Chat chat = chatRepository.findByPlayerUsername(dto.chatName());

        if(chat==null){
            chatRepository.save(new Chat(true, playerService.findById(user.getId()), new ArrayList<>(), new HashSet<>(), new HashSet<>(), dto.chatName()));
            chat = chatRepository.findByPlayerUsername(dto.chatName());
        }
        requestService.persist(new Request(dto.message(), LocalDateTime.now(), chat, MessageType.TEXT, new HashSet<>()));
//        System.out.println(chat.getPlayerUsername());


        ChatServerEvent chatEvent = new ChatServerEvent(dto.message(), dto.chatName(), users1);
        publisher.publishEvent(chatEvent);
    }

//    public SseEmitter registerClient(String name) {
//        var emitter = new SseEmitter(DEFAULT_TIMEOUT);
////        var client = new Client(emitter, name);
//
//        emitter.onCompletion(() -> registeredClients.remove(client));
//        emitter.onError((err) -> removeAndLogError(client));
//        emitter.onTimeout(() -> removeAndLogError(client));
//        registeredClients.add(client);
//        sendWelcomeToClient(client);
//
//        System.out.println("New client registeres");
//        return emitter;
//    }
}
