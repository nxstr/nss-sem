package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    public Set<Chat> getAllChatsForUser(@PathVariable String username){
        Set<Chat> chats = new HashSet<>();
        if(Objects.equals(employeeService.findByUsername(username).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")){
            chats.addAll(employeeService.findAllChats((Employee) employeeService.findByUsername(username)));
        }else{
            chats.add(chatService.findByPlayer(username));
        }
        return chats;
    }


    @GetMapping(value="/api/{username}/chat/{id}")
    public ArrayList<MessageDto> getAllMessagesForChat(@PathVariable String username, @PathVariable Integer id){
        ArrayList<MessageDto> messages = new ArrayList<>();
        for(Message m: chatService.findById(id).getMessages()){
            MessageDto messageDto = new MessageDto();
            messageDto.setMessageType("message");
            messageDto.setChat(chatService.findById(id).getPlayerUsername());
            messageDto.setContent(m.getDataPath());
            if(Objects.equals(m.getClass().getAnnotation(DiscriminatorValue.class).value(), "RESPONSE")){
                Response r = (Response) m;
                messageDto.setSender(r.getEmployee().getUsername());
                if(username.equals(messageDto.getChat())){
                    messageDto.setSender("Employee");
                }
            }else{
                messageDto.setSender(chatService.findById(id).getPlayerUsername());
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
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
