package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.model.users.User;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

@Component
public class EventHandler {

    public static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;

    private final UserRepository dao;

    @Autowired
    public EventHandler(@Qualifier("userRepository") UserRepository dao) {
        this.dao = dao;
    }

    @EventListener
    @Transactional
    public void handlerChat(ChatServerEvent event){
        System.out.println("message");
//        messageRepository.save(new MessageEntity(event.message(), dao.findByName(event.userName())));
        Set<Client> clients = Set.copyOf(event.registeredClients());
        for (Client client: clients) {
            System.out.println(client.getName());
            sendMessage(client, event);
        }
    }

    @EventListener
    @Transactional
    public void handlerWelcome(WelcomeServerEvent event){
        System.out.println("welcome");
//        if(dao.findByUsername(event.getUser().getUsername())==null) {
//            dao.save(new User(event.client().getName()));
//        }

        sendMessage(event.client(), event);
    }


    public void sendMessage(Client client, CommunicationEvent event) {
        var sseEmitter = client.getSseEmitter();
        try {
            //event handler generates sse emitter event for send
            sseEmitter.send(event().name(event.getEventType()).data(event));
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
        }
    }

}