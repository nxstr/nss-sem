package cz.cvut.fel.nss.chatgc.service.users;

import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.repository.RoleRepository;
import cz.cvut.fel.nss.chatgc.repository.users.EmployeeRepository;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;

@Service
public class EmployeeService extends UserService<Employee> {

    public static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;
    private final EmployeeRepository employeeDao;
    private final RoleRepository roleDao;

    public EmployeeService(UserRepository<Employee, Integer> userDao, ApplicationEventPublisher publisher, EmployeeRepository employeeDao, RoleRepository roleDao) {
        super(userDao, publisher);
        this.employeeDao = employeeDao;
        this.roleDao = roleDao;
    }

    @Transactional
    public Employee findById(Integer id){
        return (Employee) employeeDao.findById(id).orElse(null);
    }

    public SseEmitter registerClient(String name) {
        var emitter = new SseEmitter(DEFAULT_TIMEOUT);
        var client = new Client(emitter, name);

        if(roleDao.findByName("admin")==null) {
            roleDao.save(new Role("admin", new HashSet<>(), null, new HashSet<>()));
        }
        if(employeeDao.findByUsername(name)==null){
            persist(new Employee(name, "employeeEmail", "employeePass", roleDao.findByName("admin")));
        }

        //move to event handler maybe

        addOnlineUsers(client);
        sendWelcomeToClient(client);

        System.out.println("New client registeres");
        return emitter;
    }
}
