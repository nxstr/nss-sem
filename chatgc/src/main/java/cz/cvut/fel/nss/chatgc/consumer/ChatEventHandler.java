package cz.cvut.fel.nss.chatgc.consumer;

import cz.cvut.fel.nss.chatgc.constants.MessageTypeConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.events.ChatEvent;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Chat Event Listener.
 */
@Component
public class ChatEventHandler extends AbstractHandler{

    @Autowired
    SimpMessagingTemplate template;
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public ChatEventHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService) {
        super(simpUserRegistry, employeeService);
        this.employeeService = employeeService;
    }

    /**
     * Handles chat event and notifies all employees, that have access to this chat, about update.
     * @param event ChatEvent has event type and ChatDto
     */
    @EventListener
    @Transactional
    public void handleChatEvent(ChatEvent event){
        List<String> onlineEmps = getOnlineEmps();
        if(event.message().equals("update")){
            for(String i: onlineEmps){
                Employee e = (Employee) employeeService.findByUsername(i);
                if(e.getRole().getCategories().containsAll(event.chat().getCategories()) || e.getRole().getName().equals("admin")){
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType(MessageTypeConstants.CHAT);
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
            }
        }
    }
}
