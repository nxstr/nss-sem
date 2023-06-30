package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.constants.MessageTypeConstants;
import cz.cvut.fel.nss.chatgc.dto.CategoryDto;
import cz.cvut.fel.nss.chatgc.dto.ChatDTO;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.CategoryService;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import cz.cvut.fel.nss.chatgc.service.impl.users.PlayerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final EmployeeServiceImpl employeeService;
    private final PlayerServiceImpl playerService;
    private final ChatService chatService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private Visitor v;

    public ChatController(EmployeeServiceImpl employeeService, PlayerServiceImpl playerService, ChatService chatService, CategoryService categoryService) {
        this.employeeService = employeeService;
        this.playerService = playerService;
        this.chatService = chatService;
        this.categoryService = categoryService;
    }

    /**
     * Finds all chats for user by username.
     *
     * @param username String name of user
     * @return Set<ChatDTO>
     */
    @GetMapping(value = "/api/allChats/{username}")
    public Set<ChatDTO> getAllChatsForUser(@PathVariable String username) {
        Set<ChatDTO> chats = new HashSet<>();
        if (Objects.equals(employeeService.findByUsername(username).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
            for (Chat chat : employeeService.findAllChats((Employee) employeeService.findByUsername(username)).stream().filter(d -> d.getPlayer() != null).toList()) {
                MessageDto dto = new MessageDto();
                chats.add(setUpDto(chat, dto, username));
            }

        } else {
            MessageDto dto = new MessageDto();
            if (chatService.findByPlayer(username) != null) {
                Chat chat = chatService.findByPlayer(username);
                chats.add(setUpDto(chat, dto, username));

            }
        }
        return chats;
    }

    /**
     * Sets correct chatDTO, that depends on username.
     *
     * @param chat     Chat that contains data for dto
     * @param dto      MessageDto has client data for setUp
     * @param username String username of user that sends request
     * @return ChatDTO
     */
    private ChatDTO setUpDto(Chat chat, MessageDto dto, String username) {
        if (!chat.getMessages().isEmpty()) {
            chat.getMessages().sort(new Comparator<Message>() {
                public int compare(Message o1, Message o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            Message m = chat.getMessages().get(chat.getMessages().size() - 1);
            dto.setContent(m.getDataPath());
            dto.setChat(chat.getPlayerUsername());
            dto.setMessageType(MessageTypeConstants.MESSAGE);
            dto.setDate(m.getDate());
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
                if (r.getCategories() != null && !r.getCategories().isEmpty()) {
                    for (Category c : r.getCategories()) {
                        CategoryDto d = c.accept(v);
                        dto.getCategories().add(d);
                    }
                }
            }
        }
        return new ChatDTO(chat.isOpen(), chat.getPlayerUsername(), chat.getId(), chat.getCategories(), dto);
    }

    /**
     * finds all messages for concrete chat by its id.
     *
     * @param username String name of user, that sends request (it is needed for representing user-written messages as "my" messages on client side)
     * @param id       Integer id of chat
     * @return ArrayList<MessageDto>
     */
    @GetMapping(value = "/api/{username}/chat/{id}")
    public ArrayList<MessageDto> getAllMessagesForChat(@PathVariable String username, @PathVariable Integer id) {
        ArrayList<MessageDto> messages = new ArrayList<>();
        ArrayList<Message> msgs = chatService.findById(id).getMessages();
        msgs.sort(new Comparator<Message>() {
            public int compare(Message o1, Message o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        for (Message m : msgs) {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessageType(MessageTypeConstants.MESSAGE);
            messageDto.setChat(chatService.findById(id).getPlayerUsername());
            messageDto.setContent(m.getDataPath());
            messageDto.setDate(m.getDate());
            if (Objects.equals(m.getClass().getAnnotation(DiscriminatorValue.class).value(), "RESPONSE")) {
                Response r = (Response) m;
                if (r.getEmployee() != null) {
                    messageDto.setSender(r.getEmployee().getUsername());
                } else {
                    messageDto.setSender("deleted");
                }
                if (username.equals(messageDto.getChat())) {
                    messageDto.setSender("Employee");
                }
            } else {
                messageDto.setSender(chatService.findById(id).getPlayerUsername());
                messageDto.setCategories(new ArrayList<>());
                Request r = (Request) m;
                for (Category c : r.getCategories()) {
                    CategoryDto d = c.accept(v);
                    messageDto.getCategories().add(d);
                }
            }
            messages.add(messageDto);
        }
        return messages;
    }

    /**
     * Gets new message from user.
     *
     * @param message MessageDto has message data
     * @param chatId  Integer id of chat
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "/api/send/{chatId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> sendMessage(@RequestBody MessageDto message, @PathVariable Integer chatId) {
        try {
            message.setChat(chatService.findById(chatId).getPlayerUsername());
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_CHAT, message).get();
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Creates new chat for authorized player.
     *
     * @param principal Principal
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "api/chats/new")
    @PreAuthorize("hasAuthority('PLAYER')")
    public ResponseEntity<String> createChat(Principal principal) {
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            Integer id = auth.getPrincipal().getAccount().getId();
            Player player = playerService.findById(id);
            if (player.getChat() != null) {
                throw new ExistsException("this player already has chat");
            }
            Chat chat = new Chat(true, player, new ArrayList<>(), new HashSet<>(), new HashSet<>(), player.getUsername());
            chatService.persist(chat);
            player.setChat(chat);
            playerService.update(player);
            LOG.info("Chat {} successfully created", player.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException e) {
            LOG.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Sets chat to close state.
     *
     * @param id Integer id of chat
     * @return ResponseEntity<String>
     */
    @PutMapping(value = "api/chats/{id}/close")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<String> setChatClose(@PathVariable Integer id) {
        try {
            Chat chat = chatService.findById(id);
            chatService.setChatClose(chat);
            LOG.info("Chat {} successfully closed", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ExistsException e) {
            LOG.info(e.getMessage() + ": {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Sets categories to chat.
     *
     * @param id         Integer id of chat
     * @param categories List<CategoryDto> represents list of categories, that will be added.
     * @return ResponseEntity<String>
     */
    @PutMapping(value = "api/chats/{id}/cats")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> setCategoriesByAdmin(@PathVariable Integer id, @RequestBody List<CategoryDto> categories) {
        try {
            Chat chat = chatService.findById(id);
            categoryService.setCategoriesToChat(chat, categories);
            LOG.info("Chat {} successfully updated", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ExistsException e) {
            LOG.info(e.getMessage() + ": {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Sets chat to open state.
     *
     * @param id Integer id of chat
     * @return ResponseEntity<String>
     */
    @PutMapping(value = "api/chats/{id}/open")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> setChatOpen(@PathVariable Integer id) {
        try {
            Chat chat = chatService.findById(id);
            chatService.setChatOpen(chat);
            LOG.info("Chat {} successfully opened", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ExistsException e) {
            LOG.info(e.getMessage() + ": {}", id);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Finds chat by its id.
     *
     * @param id Integer id of chat
     * @return ChatDTO
     */
    @GetMapping(value = "api/chats/{id}/get")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ChatDTO getChat(@PathVariable Integer id) {
        Chat chat = chatService.findById(id);
        ChatDTO dto = new ChatDTO();
        dto.setId(id);
        dto.setOpen(chat.isOpen());
        dto.setPlayerUsername(chat.getPlayerUsername());
        dto.setCategories(chat.getCategories());
        return dto;
    }


}
