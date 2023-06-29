package cz.cvut.fel.nss.chatgc.consumer.kafka;

import cz.cvut.fel.nss.chatgc.controller.users.EmployeeController;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import javax.persistence.DiscriminatorValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseKafkaHandler implements KafkaHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private KafkaHandler next;
    private final String handlerType;
    private final SimpUserRegistry simpUserRegistry;
    private final EmployeeServiceImpl employeeService;

    public BaseKafkaHandler(String handlerType, SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService) {
        this.handlerType = handlerType;
        this.simpUserRegistry = simpUserRegistry;
        this.employeeService = employeeService;
    }

    @Override
    public final void setNext(KafkaHandler handler) {
        this.next = handler;
    }

    @Override
    public final void canHandle(MessageDto dto) {
        if(dto.getMessageType().equals(handlerType)){
            handle(dto);
        }else if(!dto.getMessageType().equals(handlerType) && next!=null){
            this.next.canHandle(dto);
        }
    }

    public Logger getLOG() {
        return LOG;
    }

    @Override
    public abstract void handle(MessageDto message);

    public List<String> getOnlineEmps(){
        List<String> onlineUsers = simpUserRegistry
                .getUsers()
                .stream()
                .map(SimpUser::getName).toList();
        ArrayList<String> onlineEmps = new ArrayList<>();
        for(String name: onlineUsers){
            if(Objects.equals(employeeService.findByUsername(name).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
                onlineEmps.add(name);
            }
        }
        return onlineEmps;
    }
}
