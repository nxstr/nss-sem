package cz.cvut.fel.nss.chatgc.consumer.kafka;

import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import javax.persistence.DiscriminatorValue;
import java.util.Objects;
@Component
public class LoginHandler extends BaseKafkaHandler {
    @Autowired
    SimpMessagingTemplate template;
    private final EmployeeServiceImpl employeeService;
    private final LogoutHandler logoutHandler;
    private static final String handlerType = "login";

    public LoginHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService, LogoutHandler logoutHandler) {
        super(handlerType, simpUserRegistry, employeeService);
        this.employeeService = employeeService;
        this.logoutHandler = logoutHandler;
        this.setNext(logoutHandler);
    }


    @KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC_LOGIN,
            groupId = KafkaConstants.GROUP_ID
    )
    @Override
    public void handle(MessageDto message){
        message.setMessageType("login");
        getLOG().info("{} sending via kafka-login listener..", message.getSender());
        if(employeeService.findByUsername(message.getSender())!=null && Objects.equals(employeeService.findByUsername(message.getSender()).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
            Employee e = (Employee) employeeService.findByUsername(message.getSender());
            message.setContent(e.getRole().getName());
        }else{
            message.setContent("player");
        }
        template.convertAndSend("/topic/group/"+message.getSender(), message);
    }
}
