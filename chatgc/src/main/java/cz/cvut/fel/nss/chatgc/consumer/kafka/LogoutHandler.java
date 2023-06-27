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

import javax.persistence.DiscriminatorValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Component
public class LogoutHandler extends BaseKafkaHandler{
    @Autowired
    private final EmployeeServiceImpl employeeService;
    private static final String handlerType = "logout";
    public LogoutHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService) {
        super(handlerType, simpUserRegistry, employeeService);
        this.employeeService = employeeService;
    }


    @KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC_LOGOUT,
            groupId = KafkaConstants.GROUP_ID
    )
    @Override
    public void handle(MessageDto message){
//        if(employeeService.findByUsername(message.getSender())!=null && Objects.equals(employeeService.findByUsername(message.getSender()).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
//            if (getOnlineEmps().contains(message.getSender())) {
//                ArrayList<String> emps = getOnlineEmps();
//                emps.remove(message.getSender());
//                setOnlineEmps(emps);
//            }
//        }
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication(null);
        //make logger and maybe smth else, but no more hand control of online users
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> logout"+getOnlineEmps());
    }
}
