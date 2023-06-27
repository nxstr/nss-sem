package cz.cvut.fel.nss.chatgc.consumer;


import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.model.messages.MessageType;
import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.CategoryService;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import cz.cvut.fel.nss.chatgc.service.MessageService;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.DiscriminatorValue;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class KafkaListener {
    @Autowired
    SimpMessagingTemplate template;
    private final EmployeeServiceImpl employeeService;
    @Qualifier("response")
    private final MessageService responseService;
    private final ChatService chatService;
    private final CategoryService categoryService;

    private ArrayList<String> onlineEmps = new ArrayList<>();

    public KafkaListener(EmployeeServiceImpl employeeService, @Qualifier("response") MessageService responseService, ChatService chatService, CategoryService categoryService) {
        this.employeeService = employeeService;
        this.responseService = responseService;
        this.chatService = chatService;
        this.categoryService = categoryService;
    }

    @org.springframework.kafka.annotation.KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC_CHAT,
            groupId = KafkaConstants.GROUP_ID
    )
    public void listen(MessageDto message) {
        System.out.println("sending via kafka kafka-chat listener..");

        if(!message.getSender().equals(message.getChat())){
            Response r = new Response((Employee) employeeService.findByUsername(message.getSender()));
            r.setChat(chatService.findByPlayer(message.getChat()));
            r.setDataPath(message.getContent());
            r.setDate(LocalDateTime.now());
            r.setType(MessageType.TEXT);
            message.setDate(r.getDate());
            responseService.persist(r);
            Chat chat = updateChat(message.getChat(), r);
            chatService.update(chat);
        }else{
            Set<Category> cats = new HashSet<>();
            if(message.getCategories()!=null && !message.getCategories().isEmpty()) {
                for (CategoryDto c : message.getCategories()) {
                    Category category = categoryService.findById(c.getId());
                    if (category != null) {
                        cats.add(category);
                        System.out.println(category.getName());
                    }
                }
            }
            Request r = new Request(message.getContent(), LocalDateTime.now(), chatService.findByPlayer(message.getChat()), MessageType.TEXT, cats);
            message.setDate(r.getDate());
            Chat chat = updateChat(message.getChat(), r);
            chat.setOpen(true);
            if(chat.getCategories()==null){
                chat.setCategories(new HashSet<>());
            }else{
                chat.getCategories().addAll(cats);
            }
            chatService.update(chat);
        }

        for(String i: onlineEmps){
            template.convertAndSend("/topic/group/"+i, message);
        }

        if(!message.getSender().equals(message.getChat())){
            message.setSender("Employee");
        }
        template.convertAndSend("/topic/group/"+message.getChat(), message);

    }


    public Chat updateChat(String chatName, Message r){
        Chat chat = chatService.findByPlayer(chatName);
        ArrayList<Message> mess = chat.getMessages();
        mess.add(r);
        chat.setMessages(mess);
        System.out.println("chat updated: " + chat);
        return chat;
    }



//    @org.springframework.kafka.annotation.KafkaListener(
//            topics = KafkaConstants.KAFKA_TOPIC_LOGIN,
//            groupId = KafkaConstants.GROUP_ID
//    )
//    public void listenLogin(MessageDto message){
//        message.setMessageType("login");
//        System.out.println("sending via kafka login-topic listener.." + message.getSender());
//        if(employeeService.findByUsername(message.getSender())!=null && Objects.equals(employeeService.findByUsername(message.getSender()).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
//            if (!onlineEmps.contains(message.getSender())){
//                onlineEmps.add(message.getSender());
//            }
//            Employee e = (Employee) employeeService.findByUsername(message.getSender());
//            message.setContent(e.getRole().getName());
//        }else{
//            message.setContent("player");
//        }
//        template.convertAndSend("/topic/group/"+message.getSender(), message);
//    }

//    @org.springframework.kafka.annotation.KafkaListener(
//            topics = KafkaConstants.KAFKA_TOPIC_LOGOUT,
//            groupId = KafkaConstants.GROUP_ID
//    )
//    public void listenLogout(MessageDto message){
//        if(employeeService.findByUsername(message.getSender())!=null && Objects.equals(employeeService.findByUsername(message.getSender()).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
//            if (onlineEmps.contains(message.getSender())) {
//                onlineEmps.remove(message.getSender());
//            }
//        }
////            message.setContent("employee");
////        }else{
////            message.setContent("player");
////        }
////        template.convertAndSend("/topic/group/"+message.getSender(), message);
////        System.out.println("logged out and need to remade logout on react to setUser(null) on message type logout received through websocket session " + message.getSender());
//    }




}
