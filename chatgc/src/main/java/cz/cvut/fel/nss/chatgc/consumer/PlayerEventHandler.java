package cz.cvut.fel.nss.chatgc.consumer;

import cz.cvut.fel.nss.chatgc.constants.MessageTypeConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.events.PlayerEvent;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import cz.cvut.fel.nss.chatgc.service.impl.utils.DefaultEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Player Event Listener.
 */
@Component
public class PlayerEventHandler extends AbstractHandler{
    @Autowired
    SimpMessagingTemplate template;
    private final DefaultEmailService emailService;

    @Autowired
    public PlayerEventHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService, DefaultEmailService emailService) {
        super(simpUserRegistry, employeeService);
        this.emailService = emailService;
    }

    /**
     * Handles player event. According to type of event, forces employees to actualise data, sends emails or forces logout player.
     * @param event PlayerEvent has event type and PlayerDto
     */
    @EventListener
    @Transactional
    public void handlePlayerEvent(PlayerEvent event){
        switch (event.message()) {
            case "create" -> emailService.sendSimpleEmail(event.dto().getEmail(), "Your account data", "You have a new account in the GC web-chat app, here are your data:\n" +
                    "username: " + event.dto().getUsername() + ",\n" +
                    "password: " + event.dto().getPassword() + "\n");
            case "updateData" -> {
                MessageDto messageDto = new MessageDto();
                messageDto.setMessageType(MessageTypeConstants.LOGOUT);
                template.convertAndSend("/topic/group/" + event.dto().getUsername(), messageDto);
            }
            case "updateUsername" -> {
                emailService.sendSimpleEmail(event.dto().getEmail(), "Your account data", "Your account has been changed in the GC web-chat app, here are your data:\n" +
                        "username: " + event.dto().getUsername() + "\n");
                for (String i : getOnlineEmps()) {
                    MessageDto message = new MessageDto();
                    message.setMessageType(MessageTypeConstants.CHAT);
                    template.convertAndSend("/topic/group/" + i, message);
                }
            }
            case "updatePass" -> emailService.sendSimpleEmail(event.dto().getEmail(), "Your account data", "Your password has been changed in the GC web-chat app, here are your new data:\n" +
                    "password: " + event.dto().getPassword() + "\n");
        }
    }
}
