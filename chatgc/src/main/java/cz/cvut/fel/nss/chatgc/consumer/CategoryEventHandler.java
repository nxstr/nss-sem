package cz.cvut.fel.nss.chatgc.consumer;

import cz.cvut.fel.nss.chatgc.constants.MessageTypeConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.events.CategoryEvent;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Category Event Listener.
 */
@Component
public class CategoryEventHandler extends AbstractHandler {
    @Autowired
    SimpMessagingTemplate template;
    private final RoleService roleService;
    private final EmployeeServiceImpl employeeService;

    @Autowired
    public CategoryEventHandler(SimpUserRegistry simpUserRegistry, RoleService roleService, EmployeeServiceImpl employeeService) {
        super(simpUserRegistry, employeeService);
        this.roleService = roleService;
        this.employeeService = employeeService;
    }

    /**
     * Handles category event.
     *
     * @param event CategoryEvent contains event type and CategoryDto
     */
    @EventListener
    @Transactional
    public void handleCategory(CategoryEvent event) {
        List<String> onlineEmps = getOnlineEmps();
        if (event.message().equals("delete")) {
            List<Role> roles = roleService.deleteCategoryInAllRoles(event.category());
            notifyEmployees(onlineEmps, roles);
        } else if (event.message().equals("changeCatIntoRole")) {
            List<Role> roles = roleService.changeCategoryInAllRoles(event.category());
            notifyEmployees(onlineEmps, roles);
        }
    }

    /**
     * Part of event processing.
     * Notifies all online employees, that had the access to old category and force users to update their current data.
     *
     * @param onlineEmps List<String> names of online employees.
     * @param roles      List<Role> that contained old category and need to be updated
     */
    public void notifyEmployees(List<String> onlineEmps, List<Role> roles) {
        for (String i : onlineEmps) {
            Employee e = (Employee) employeeService.findByUsername(i);
            if (roles.contains(e.getRole())) {
                MessageDto messageDto = new MessageDto();
                messageDto.setMessageType(MessageTypeConstants.CHAT);
                template.convertAndSend("/topic/group/" + i, messageDto);
            }
        }
    }
}
