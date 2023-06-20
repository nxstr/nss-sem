package cz.cvut.fel.nss.chatgc.service.users;

import cz.cvut.fel.nss.chatgc.dto.EmployeeDTO;
import cz.cvut.fel.nss.chatgc.events.EmployeeEvent;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.User;
import cz.cvut.fel.nss.chatgc.repository.ChatRepository;
import cz.cvut.fel.nss.chatgc.repository.RoleRepository;
import cz.cvut.fel.nss.chatgc.repository.users.EmployeeRepository;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import cz.cvut.fel.nss.chatgc.service.RoleService;
import cz.cvut.fel.nss.chatgc.service.messages.ResponseService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import java.util.*;

@Service
public class EmployeeService extends UserService<Employee> {

    public static final long DEFAULT_TIMEOUT = Long.MAX_VALUE;
    private final EmployeeRepository employeeDao;
    private final ChatRepository chatRepository;
    private final PasswordEncoder encoder;
    private final ResponseService responseService;
    private final ApplicationEventPublisher publisher;

    public EmployeeService(UserRepository<Employee, Integer> userDao, ApplicationEventPublisher publisher, EmployeeRepository employeeDao, ChatRepository chatRepository, PasswordEncoder encoder, PasswordEncoder encoder1, ResponseService responseService, ApplicationEventPublisher publisher1) {
        super(userDao, publisher, encoder);
        this.employeeDao = employeeDao;
        this.chatRepository = chatRepository;
        this.encoder = encoder1;
        this.responseService = responseService;
        this.publisher = publisher1;
    }

    @Transactional
    public void create(Employee employee){
        if(this.findByUsername(employee.getUsername())!=null){
            System.out.println("here");
            throw new ExistsException("username already exists");
        }
        if(this.findByEmail(employee.getEmail())!=null){
            throw new ExistsException("email already exists");
        }
        if(employee.getRole()==null){
            throw new RoleException("employee dont have role");
        }
        publisher.publishEvent(new EmployeeEvent("create", new EmployeeDTO(employee.getUsername(), employee.getPassword(), employee.getEmail(), employee.getRole().getId())));
        this.persist(employee);
    }


    public List<Employee> findAllEmployees(){
        List<Employee> emps = new ArrayList<>();
        for(User e: employeeDao.findAll()){
            if(Objects.equals(e.getClass().getAnnotation(DiscriminatorValue.class).value(), "EMPLOYEE")){
                Employee employee = (Employee) e;
                emps.add(employee);
            }
        }
        return emps;
    }



    @Transactional
    public void changeUsername(Employee employee, String newName){
        if(findByUsername(newName)!=null){
            throw new ExistsException("username already exists");
        }
        publisher.publishEvent(new EmployeeEvent("changeUsername", new EmployeeDTO(employee.getUsername(), employee.getPassword(), employee.getEmail(), employee.getRole().getId())));
        employee.setUsername(newName);
        for(Response r: employee.getResponses()){
            r.setEmployee(employee);
            responseService.update(r);
        }
        update(employee);
        publisher.publishEvent(new EmployeeEvent("changeData", new EmployeeDTO(employee.getUsername(), employee.getPassword(), employee.getEmail(), employee.getRole().getId())));
    }

//    @Transactional
//    public void changeEmail(Employee employee, String email){
//        super.changeEmail(employee, email);
//        publisher.publishEvent(new EmployeeEvent("changeData", new EmployeeDTO(employee.getUsername(), employee.getPassword(), employee.getEmail(), employee.getRole().getId())));
//    }

    @Transactional
    public void changeEmployeePassword(Employee employee, String password){
        publisher.publishEvent(new EmployeeEvent("changePass", new EmployeeDTO(employee.getUsername(), password, employee.getEmail(), employee.getRole().getId())));
        changePassword(employee, password);
    }

    @Transactional
    public void delete(Employee employee){
        for(Response r: employee.getResponses()){
            r.setEmployee(null);
        }
        employeeDao.delete(employee);
        publisher.publishEvent(new EmployeeEvent("delete", new EmployeeDTO(employee.getUsername(), employee.getPassword(), employee.getEmail(), employee.getRole().getId())));
    }

    @Transactional
    public void changeRole(Employee employee, Role role){
        if(employee.getRole()==null){
            throw new RoleException("employee does not have any role");
        }
        employee.setRole(role);
        update(employee);
        publisher.publishEvent(new EmployeeEvent("change", new EmployeeDTO(employee.getUsername(), employee.getPassword(), employee.getEmail(), employee.getRole().getId())));
    }


    @Transactional
    @Override
    public Employee findById(Integer id){
        return (Employee) employeeDao.findById(id).orElse(null);
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
        }
        return chats;
    }


    @Transactional
    public void updateEmployeeFromDto(EmployeeDTO dto, Integer id){
        Employee employee = findById(id);
        if(!employee.getUsername().equals(dto.getUsername())){
            changeUsername(employee, dto.getUsername());
        }
        if(!employee.getEmail().equals(dto.getEmail())){
            changeEmail(employee, dto.getEmail());
        }
        if(!encoder.matches(dto.getPassword(), employee.getPassword())){
            changeEmployeePassword(employee, dto.getPassword());
        }
    }
}
