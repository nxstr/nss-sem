package cz.cvut.fel.nss.chatgc.consumer;


import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.model.messages.MessageType;
import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import cz.cvut.fel.nss.chatgc.service.messages.RequestService;
import cz.cvut.fel.nss.chatgc.service.messages.ResponseService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.DiscriminatorValue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

@Component
public class MessageListener {
    @Autowired
    SimpMessagingTemplate template;
//    ChatController controller;
    private final EmployeeService employeeService;
    private final RequestService requestService;
    private final ResponseService responseService;
    private final ChatService chatService;
    private HashMap<String, ArrayList<String>> chats = new HashMap<>(); //на які чати підписані які юзери, ключ - нікнейм, значення - список чат айді

    private ArrayList<String> onlineEmps = new ArrayList<>();

    public MessageListener(EmployeeService employeeService, RequestService requestService, ResponseService responseService, ChatService chatService) {
        this.employeeService = employeeService;
        this.requestService = requestService;
        this.responseService = responseService;
        this.chatService = chatService;
    }

    @KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC,
            groupId = KafkaConstants.GROUP_ID
    )
    public void listen(MessageDto message) {
        System.out.println("sending via kafka kafka-chat-3 listener..");

        if(!message.getSender().equals(message.getChat())){
            Response r = new Response((Employee) employeeService.findByUsername(message.getSender()));
            r.setChat(chatService.findByPlayer(message.getChat()));
            r.setDataPath(message.getContent());
            responseService.persist(r);
            Chat chat = chatService.findByPlayer(message.getChat());
            ArrayList<Message> mess = chat.getMessages();
            mess.add(r);
            chat.setMessages(mess);
            chatService.update(chat);
            System.out.println("messages update "+chat.getMessages());
        }else{
            Request r = new Request(message.getContent(), LocalDateTime.now(), chatService.findByPlayer(message.getChat()), MessageType.TEXT, new HashSet<>());
//            r.setChat(chatService.findByPlayer(message.getChat()));
//            r.setDataPath(message.getContent());
            requestService.persist(r);
            Chat chat = chatService.findByPlayer(message.getChat());
            ArrayList<Message> mess = chat.getMessages();
            mess.add(r);
            chat.setMessages(mess);
            chatService.update(chat);
            System.out.println("messages update "+chat.getMessages());
        }



        for(String i: onlineEmps){
            System.out.println("send to employee " + i);
            template.convertAndSend("/topic/group/"+i, message);
        }

        if(!message.getSender().equals(message.getChat())){
            System.out.println("here " + message.getSender());
            message.setSender("Employee");
        }
        System.out.println("send to player " + message.getChat());
        template.convertAndSend("/topic/group/"+message.getChat(), message);

    }



    @KafkaListener(
            topics = "login-topic",
            groupId = KafkaConstants.GROUP_ID
    )
    public void listenLogin(MessageDto message){

        System.out.println("sending via kafka login-topic listener.." + message.getSender());
        if(employeeService.findByUsername(message.getSender())!=null && Objects.equals(employeeService.findByUsername(message.getSender()).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
            if (!onlineEmps.contains(message.getSender())){
                onlineEmps.add(message.getSender());
            }
        }
        System.out.println("-------------------------------- username key " + message.getSender());
    }

}
