package cz.cvut.fel.nss.chatgc.consumer;


import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.events.CategoryEvent;
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
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import java.time.LocalDateTime;
import java.util.*;

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
            r.setDate(LocalDateTime.now());
            r.setType(MessageType.TEXT);
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
        message.setMessageType("login");
        System.out.println("sending via kafka login-topic listener.." + message.getSender());
        if(employeeService.findByUsername(message.getSender())!=null && Objects.equals(employeeService.findByUsername(message.getSender()).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
            if (!onlineEmps.contains(message.getSender())){
                onlineEmps.add(message.getSender());
            }
            message.setContent("employee");
        }else{
            message.setContent("player");
        }
        template.convertAndSend("/topic/group/"+message.getSender(), message);
        System.out.println("-------------------------------- username key " + message.getSender());
    }




    //if emp's role has category, that has been updated
    //fun will send notification to user's topic
    @EventListener
    @Transactional
    public void handleCategory(CategoryEvent event){
        System.out.println("category event");
        if(event.message().equals("update") || event.message().equals("delete")){
            for(String i: onlineEmps){
                Employee e = (Employee) employeeService.findByUsername(i);
                if(e.getRole().getCategories().contains(event.category())) {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType("chatListUpdate");
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
            }
        }
    }

}
