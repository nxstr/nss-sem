package cz.cvut.fel.nss.chatgc.consumer;

import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;

import javax.persistence.DiscriminatorValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * General handler class. Serves for getting actual online employees.
 */

public abstract class AbstractHandler {

    private final SimpUserRegistry simpUserRegistry;
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public AbstractHandler(SimpUserRegistry simpUserRegistry, EmployeeServiceImpl employeeService) {
        this.simpUserRegistry = simpUserRegistry;
        this.employeeService = employeeService;
    }

    /**
     * Finds list of authorized employees' names.
     * Using List<String> because users subscribes to topics that are identical to their usernames.
     *
     * @return List<String>
     */
    public List<String> getOnlineEmps() {
        List<String> onlineUsers = simpUserRegistry
                .getUsers()
                .stream()
                .map(SimpUser::getName).toList();
        ArrayList<String> onlineEmps = new ArrayList<>();
        for (String name : onlineUsers) {
            if (Objects.equals(employeeService.findByUsername(name).getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")) {
                onlineEmps.add(name);
            }
        }
        return onlineEmps;
    }
}
