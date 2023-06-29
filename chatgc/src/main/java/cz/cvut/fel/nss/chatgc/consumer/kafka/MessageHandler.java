package cz.cvut.fel.nss.chatgc.consumer.kafka;

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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
public class MessageHandler extends BaseKafkaHandler{
    @Autowired
    SimpMessagingTemplate template;
    private final EmployeeServiceImpl employeeService;
    @Qualifier("response")
    private final MessageService responseService;
    private final ChatService chatService;
    @Autowired
    private final CategoryService categoryService;
    private static final String handlerType = "message";

    public MessageHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService, EmployeeServiceImpl employeeService1, @Qualifier("response") MessageService responseService, ChatService chatService, CategoryService categoryService) {
        super(handlerType, simpUserRegistry, employeeService);
        this.employeeService = employeeService1;
        this.responseService = responseService;
        this.chatService = chatService;
        this.categoryService = categoryService;
    }

    @KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC_CHAT,
            groupId = KafkaConstants.GROUP_ID
    )
    public void handle(MessageDto message) {
        getLOG().info("{} sending via kafka-chat listener..", message.getSender());

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
            getLOG().info("Chat {} updated. New response from {} added: {} ", chat.getPlayerUsername(), message.getSender(), message.getContent());
        }else{
            Set<Category> cats = new HashSet<>();
            if(message.getCategories()!=null && !message.getCategories().isEmpty()) {
                for (CategoryDto c : message.getCategories()) {
                    Category category = categoryService.findById(c.getId());
                    if (category != null) {
                        cats.add(category);
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
            getLOG().info("Chat {} updated. New request from {} added: {} ", chat.getPlayerUsername(), message.getSender(), message.getContent());
            getLOG().info("Chat {} has categories: {} ", chat.getPlayerUsername(), message.getCategories().stream().map(CategoryDto::getName).toList());
        }

        for(String i: getOnlineEmps()){
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
        return chat;
    }
}
