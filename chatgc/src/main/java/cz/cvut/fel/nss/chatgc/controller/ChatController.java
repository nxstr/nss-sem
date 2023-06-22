package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.dto.ChatDTO;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.security.SecurityUtils;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import cz.cvut.fel.nss.chatgc.service.users.EmployeeService;
import cz.cvut.fel.nss.chatgc.service.users.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.DiscriminatorValue;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
public class ChatController {

    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final EmployeeService employeeService;
    private final PlayerService playerService;
    private final ChatService chatService;

    public ChatController(EmployeeService employeeService, PlayerService playerService, ChatService chatService) {
        this.employeeService = employeeService;
        this.playerService = playerService;
        this.chatService = chatService;
    }

    @GetMapping(value="/api/allChats/{username}")
    public Set<ChatDTO> getAllChatsForUser(@PathVariable String username){
        Set<ChatDTO> chats = new HashSet<>();
        System.out.println("find chat: " + chatService.findByPlayer("e"));
        if(Objects.equals(employeeService.findByUsername(username).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")){
            for(Chat chat:employeeService.findAllChats((Employee) employeeService.findByUsername(username)).stream().filter(d -> d.getPlayer()!=null).toList()){
                Message m = null;
                MessageDto dto = new MessageDto();
                chats.add(setUpDto(chat, m, dto, username));
            }

        }else{
            Message m = null;
            MessageDto dto = new MessageDto();
            if(chatService.findByPlayer(username)!=null) {
                Chat chat = chatService.findByPlayer(username);
                chats.add(setUpDto(chat, m, dto, username));

            }
        }
        return chats;
    }

    private ChatDTO setUpDto(Chat chat, Message m, MessageDto dto, String username){
        if (!chat.getMessages().isEmpty()) {
            chat.getMessages().sort(new Comparator<Message>() {
                public int compare(Message o1, Message o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            m = chat.getMessages().get(chat.getMessages().size() - 1);
            dto.setContent(m.getDataPath());
            dto.setChat(chat.getPlayerUsername());
            dto.setMessageType("message");
            if (Objects.equals(m.getClass().getAnnotation(DiscriminatorValue.class).value(), "RESPONSE")) {
                Response r = (Response) m;
                if (r.getEmployee() != null) {
                    dto.setSender(r.getEmployee().getUsername());
                } else {
                    dto.setSender("deleted");
                }
                if (username.equals(dto.getChat())) {
                    dto.setSender("Employee");
                }
            } else {
                dto.setSender(chat.getPlayerUsername());
                dto.setCategories(new ArrayList<>());
                Request r = (Request) m;
                for(Category c: r.getCategories()){
                    CategoryDto d = new CategoryDto(c.getId(), c.getName());
                    dto.getCategories().add(d);
                }
            }
        }
        return new ChatDTO(chat.isOpen(), chat.getPlayerUsername(), chat.getId(), chat.getCategories(), chat.getFolders(), dto);
    }


    @GetMapping(value="/api/{username}/chat/{id}")
    public ArrayList<MessageDto> getAllMessagesForChat(@PathVariable String username, @PathVariable Integer id){
        ArrayList<MessageDto> messages = new ArrayList<>();
        ArrayList<Message> msgs = chatService.findById(id).getMessages();
        msgs.sort(new Comparator<Message>() {
            public int compare(Message o1, Message o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        for(Message m: msgs){
            MessageDto messageDto = new MessageDto();
            messageDto.setMessageType("message");
            messageDto.setChat(chatService.findById(id).getPlayerUsername());
            messageDto.setContent(m.getDataPath());
            if(Objects.equals(m.getClass().getAnnotation(DiscriminatorValue.class).value(), "RESPONSE")){
                Response r = (Response) m;
                if(r.getEmployee()!=null) {
                    messageDto.setSender(r.getEmployee().getUsername());
                }else{
                    messageDto.setSender("deleted");
                }
                if(username.equals(messageDto.getChat())){
                    messageDto.setSender("Employee");
                }
            }else{
                messageDto.setSender(chatService.findById(id).getPlayerUsername());
                messageDto.setCategories(new ArrayList<>());
                Request r = (Request) m;
                for(Category c: r.getCategories()){
                    CategoryDto d = new CategoryDto(c.getId(), c.getName());
                    messageDto.getCategories().add(d);
                }
            }
            messages.add(messageDto);
        }
        return messages;
    }

    @PostMapping(value = "/api/send/{chatId}", consumes = "application/json", produces = "application/json")
    public void sendMessage(@RequestBody MessageDto message, @PathVariable Integer chatId) {
        try {
            message.setChat(chatService.findById(chatId).getPlayerUsername());
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, message).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "api/chats/new")
    @PreAuthorize("hasAuthority('PLAYER')")
    public ResponseEntity createChat(Principal principal){
        final AuthenticationToken auth = (AuthenticationToken) principal;
        Integer id = auth.getPrincipal().getAccount().getId();
        Player player = playerService.findById(id);
        Chat chat = new Chat(true, player, new ArrayList<>(), new HashSet<>(), new HashSet<>(), player.getUsername());
        chatService.persist(chat);
        player.setChat(chat);
        playerService.update(player);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
