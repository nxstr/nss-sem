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
