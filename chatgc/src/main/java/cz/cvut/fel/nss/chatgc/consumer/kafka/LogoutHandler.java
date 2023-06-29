package cz.cvut.fel.nss.chatgc.consumer.kafka;

import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandler extends BaseKafkaHandler{
    @Autowired
    private static final String handlerType = "logout";
    private final MessageHandler messageHandler;
    public LogoutHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService, MessageHandler messageHandler) {
        super(handlerType, simpUserRegistry, employeeService);
        this.messageHandler = messageHandler;
        this.setNext(messageHandler);
    }


    @KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC_LOGOUT,
            groupId = KafkaConstants.GROUP_ID
    )
    @Override
    public void handle(MessageDto message){
        getLOG().info("{} sending via kafka-logout listener..", message.getSender());
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication(null);
    }
}
