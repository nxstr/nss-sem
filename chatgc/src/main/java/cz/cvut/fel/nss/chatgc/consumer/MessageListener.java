package cz.cvut.fel.nss.chatgc.consumer;


import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.events.CategoryEvent;
import cz.cvut.fel.nss.chatgc.events.EmployeeEvent;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.model.messages.MessageType;
import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import cz.cvut.fel.nss.chatgc.service.utils.DefaultEmailService;
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
    private final EmployeeService employeeService;
    private final RequestService requestService;
    private final ResponseService responseService;
    private final ChatService chatService;
    private final DefaultEmailService emailService;
    private final RoleService roleService;

    private ArrayList<String> onlineEmps = new ArrayList<>();

    public MessageListener(EmployeeService employeeService, RequestService requestService, ResponseService responseService, ChatService chatService, DefaultEmailService emailService, RoleService roleService) {
        this.employeeService = employeeService;
        this.requestService = requestService;
        this.responseService = responseService;
        this.chatService = chatService;
        this.emailService = emailService;
        this.roleService = roleService;
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
            updateChat(message.getChat(), r);
        }else{
            Request r = new Request(message.getContent(), LocalDateTime.now(), chatService.findByPlayer(message.getChat()), MessageType.TEXT, new HashSet<>());
            updateChat(message.getChat(), r);
        }

        for(String i: onlineEmps){
            template.convertAndSend("/topic/group/"+i, message);
        }

        if(!message.getSender().equals(message.getChat())){
            message.setSender("Employee");
        }
        template.convertAndSend("/topic/group/"+message.getChat(), message);

    }


    public void updateChat(String chatName, Message r){
        Chat chat = chatService.findByPlayer(chatName);
        ArrayList<Message> mess = chat.getMessages();
        mess.add(r);
        chat.setMessages(mess);
        chatService.update(chat);
        System.out.println("chat updated: " + chat);
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
    }

    @KafkaListener(
            topics = "logout-topic",
            groupId = KafkaConstants.GROUP_ID
    )
    public void listenLogout(MessageDto message){
        if(employeeService.findByUsername(message.getSender())!=null && Objects.equals(employeeService.findByUsername(message.getSender()).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
            if (onlineEmps.contains(message.getSender())){
                onlineEmps.remove(message.getSender());
            }
            message.setContent("employee");
        }else{
            message.setContent("player");
        }
        for(String e: onlineEmps){
            System.out.println(e + "-----------------------");
        }
//        template.convertAndSend("/topic/group/"+message.getSender(), message);
        System.out.println("logged out and need to remade logout on react to setUser(null) on message type logout received through websocket session " + message.getSender());
    }




    //if emp's role has category, that has been updated
    //fun will send notification to user's topic
    //NOT TESTED WEBSOCKET CONNECT!!!
    @EventListener
    @Transactional
    public void handleCategory(CategoryEvent event){
        System.out.println("category event");
        if(event.message().equals("delete")){
            List<Role> roles = roleService.deleteCategoryInAllRoles(event.category());
            for(String i: onlineEmps){
                Employee e = (Employee) employeeService.findByUsername(i);
                if(roles.contains(e.getRole())){
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType("chatListUpdate");
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
            }
        }
        else if(event.message().equals("changeCatIntoRole")){
            List<Role> roles = roleService.changeCategoryInAllRoles(event.category());
            for(String i: onlineEmps){
                Employee e = (Employee) employeeService.findByUsername(i);
                if(roles.contains(e.getRole())){
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType("chatListUpdate");
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
            }
        }
    }


    //NOT TESTED WEBSOCKET CONNECT!!!
    //(email works OK)
    @EventListener
    @Transactional
    public void handleEmployeeEvent(EmployeeEvent event){
        switch (event.message()) {
            case "create":
                emailService.sendSimpleEmail(event.employee().getEmail(), "Your account data", "You have a new account in the GC web-chat app, here are your data:\n" +
                        "username: " + event.employee().getUsername() + ",\n" +
                        "password: " + event.employee().getPassword() + "\n");
                break;
            case "changeData":
                emailService.sendSimpleEmail(event.employee().getEmail(), "Your account data", "Your account has been changed in the GC web-chat app, here are your data:\n" +
                        "username: " + event.employee().getUsername() + "\n");
                for (String i : onlineEmps) {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType("chatListUpdate");
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
                break;
            case "changePass":
                emailService.sendSimpleEmail(event.employee().getEmail(), "Your account data", "Your password has been changed in the GC web-chat app, here are your new data:\n" +
                        "password: " + event.employee().getPassword() + ",\n");
                break;
            case "change":
                //update role or smth that see only updated employee
                if (onlineEmps.contains(event.employee().getUsername())) {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType("chatListUpdate");
                    template.convertAndSend("/topic/group/" + event.employee().getUsername(), messageDto);
                }
                break;
            case "delete":
                for (String i : onlineEmps) {
                    MessageDto messageDto = new MessageDto();
                    if (i.equals(event.employee().getUsername())) {
                        messageDto.setMessageType("forceLogout");
                    }else{
                        messageDto.setMessageType("chatListUpdate");
                    }
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
                break;
            case "changeUsername":
                for (String i : onlineEmps) {
                    MessageDto messageDto = new MessageDto();
                    if (i.equals(event.employee().getUsername())) {
                        messageDto.setMessageType("forceLogout");
                        template.convertAndSend("/topic/group/" + i, messageDto);
                        break;
                    }
                }
                break;
        }
    }

}
