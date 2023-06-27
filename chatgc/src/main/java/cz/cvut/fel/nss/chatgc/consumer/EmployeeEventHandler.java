package cz.cvut.fel.nss.chatgc.consumer;

import cz.cvut.fel.nss.chatgc.constants.MessageTypeConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.events.EmployeeEvent;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import cz.cvut.fel.nss.chatgc.service.impl.utils.DefaultEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EmployeeEventHandler extends AbstractHandler{
    @Autowired
    SimpMessagingTemplate template;
    private final DefaultEmailService emailService;

    @Autowired
    public EmployeeEventHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService, DefaultEmailService emailService) {
        super(simpUserRegistry, employeeService);
        this.emailService = emailService;
    }

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
                for (String i : getOnlineEmps()) {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType(MessageTypeConstants.CHAT);
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
                break;
            case "changePass":
                emailService.sendSimpleEmail(event.employee().getEmail(), "Your account data", "Your password has been changed in the GC web-chat app, here are your new data:\n" +
                        "password: " + event.employee().getPassword() + "\n");
                break;
            case "change":
                //update role or smth that see only updated employee
                if (getOnlineEmps().contains(event.employee().getUsername())) {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setMessageType(MessageTypeConstants.CHAT);
                    template.convertAndSend("/topic/group/" + event.employee().getUsername(), messageDto);
                }
                break;
            case "delete":
                for (String i : getOnlineEmps()) {
                    MessageDto messageDto = new MessageDto();
                    if (i.equals(event.employee().getUsername())) {
                        messageDto.setMessageType(MessageTypeConstants.LOGOUT);
                    }else{
                        messageDto.setMessageType(MessageTypeConstants.CHAT);
                    }
                    template.convertAndSend("/topic/group/" + i, messageDto);
                }
                break;
            case "changeUsername":
                for (String i : getOnlineEmps()) {
                    MessageDto messageDto = new MessageDto();
                    if (i.equals(event.employee().getUsername())) {
                        messageDto.setMessageType(MessageTypeConstants.LOGOUT);
                        System.out.println(messageDto.getMessageType() + " " + i);
                        template.convertAndSend("/topic/group/" + i, messageDto);
                        break;
                    }
                }
                break;
        }
    }
}
