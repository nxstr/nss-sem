package cz.cvut.fel.nss.chatgc.service.users;

import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.repository.ChatRepository;
import cz.cvut.fel.nss.chatgc.repository.RoleRepository;
import cz.cvut.fel.nss.chatgc.repository.users.EmployeeRepository;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.Set;

@Service
public class EmployeeService extends UserService<Employee> {

    public static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;
    private final EmployeeRepository employeeDao;
    private final RoleRepository roleDao;
    private final ChatRepository chatRepository;

    public EmployeeService(UserRepository<Employee, Integer> userDao, ApplicationEventPublisher publisher, EmployeeRepository employeeDao, RoleRepository roleDao, ChatRepository chatRepository) {
        super(userDao, publisher);
        this.employeeDao = employeeDao;
        this.roleDao = roleDao;
        this.chatRepository = chatRepository;
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

    public Set<Chat> findAllChats(Employee employee){
        Set<Chat> chats = new HashSet<>();
        if(!employee.getRole().getName().equals("admin")) {
            Set<Category> cats = employee.getRole().getCategories();
            for (Category c : cats) {
                chats.addAll(chatRepository.findChatsByCategories(c));
            }
        }else{
            System.out.println("here " + employee.getUsername() + " " + employee.getRole());
            for(Chat i: chatRepository.findAll()){
                System.out.println(i + " chat");
                chats.add(i);
            }
//            chats.addAll(chatRepository.findAll());
        }
        return chats;
    }
}
