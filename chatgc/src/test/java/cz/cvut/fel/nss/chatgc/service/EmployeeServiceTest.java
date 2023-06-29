package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.DataGenerator;
import cz.cvut.fel.nss.chatgc.exceptions.AppException;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.exceptions.RoleException;
import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.messages.MessageType;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.repository.CategoryRepository;
import cz.cvut.fel.nss.chatgc.repository.RoleRepository;
import cz.cvut.fel.nss.chatgc.repository.users.EmployeeRepository;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Executable;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private CategoryRepository categoryDao;

    @Mock
    private RoleRepository roleDao;

    @Mock
    private EmployeeRepository employeeDao;

    @Mock
    @Qualifier("response")
    private MessageService responseService;

    @Mock
    private ChatService chatService;

    @Mock
    PasswordEncoder encoder;

    @Mock
    private ApplicationEventPublisher publisher;

    @Captor
    private ArgumentCaptor<Employee> userArgumentCaptor;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    public void createTestReturnsException(){
        Employee e1 = setUpEmployee();
        e1.setRole(null);
        Throwable throwable = catchThrowable(() -> employeeService.create(e1));

        assertThat(throwable).isInstanceOf(AppException.class);
    }

    @Test
    public void createTest(){
        Employee e = setUpEmployee();
        employeeService.create(e);
        Mockito.verify(employeeDao, Mockito.times(1)).save(userArgumentCaptor.capture());
        Employee employee = userArgumentCaptor.getValue();
        assertEquals(e.getUsername(), employee.getUsername());
        assertEquals(e.getEmail(), employee.getEmail());
        assertEquals(e.getRole(), employee.getRole());
    }

    @Test
    public void updateEmployee(){
        Employee e = setUpEmployee();
        employeeService.persist(e);
        e.setResponses(setUpResponses(e));
        employeeService.update(e);
        Mockito.verify(employeeDao, Mockito.times(2)).save(userArgumentCaptor.capture());
        Employee employee = userArgumentCaptor.getValue();
        assertEquals(4, employee.getResponses().size());
        assertEquals(e.getResponses(), employee.getResponses());
    }

    @Test
    public void updateUsername(){
        Employee e = setUpEmployee();
        employeeService.persist(e);
        e.setResponses(setUpResponses(e));
        employeeService.update(e);

        employeeService.changeUsername(e, "testUsername");

        for(Response r: e.getResponses()){
            assertEquals("testUsername", r.getEmployee().getUsername());
        }
        assertEquals("testUsername", e.getUsername());
    }

    @Test
    public void changeRoleTest(){
        Employee e = setUpEmployee();
        employeeService.persist(e);
        Role r = setUpRole(3);
        employeeService.changeRole(e, r);

        Mockito.verify(employeeDao, Mockito.times(2)).save(userArgumentCaptor.capture());
        Employee employee = userArgumentCaptor.getValue();
        assertEquals(r, employee.getRole());

    }

    @Test
    public void deleteTest(){
        Employee e = setUpEmployee();
        employeeService.persist(e);
        List<Response> responses = setUpResponses(e);
        e.setResponses(responses);
        employeeService.update(e);

        employeeService.delete(e);

        for(Response r: responses){
            assertNull(r.getEmployee());
        }
        Mockito.verify(employeeDao, Mockito.times(1)).delete(e);
    }

    private List<Response> setUpResponses(Employee e){
        List<Response> responses = new ArrayList<>();
        Chat chat = new Chat(true, null, new ArrayList<>(), new HashSet<>(), new HashSet<>(), "test");
        chatService.persist(chat);
        for(int i=0; i<4; i++){
            Response r = new Response(e);
            r.setDataPath("test"+new Random().nextInt(10));
            r.setType(MessageType.TEXT);
            r.setDate(LocalDateTime.now());
            r.setChat(chat);
            responseService.persist(r);
            chat.getMessages().add(r);
            responses.add(r);
        }
        chatService.update(chat);
        return responses;
    }

    private Employee setUpEmployee(){
        Employee employee = DataGenerator.generateEmployee();
        employee.setRole(setUpRole(new Random().nextInt(5)));
        employee.setResponses(new ArrayList<>());
        return employee;
    }

    private Role setUpRole(int countCats){
        Set<Category> cats = new HashSet<>();
        for(int i=0; i<countCats; i++){
            Category cat1 = DataGenerator.generateCategory();
            categoryDao.save(cat1);
            cats.add(cat1);
        }
        Role role = DataGenerator.generateEmptyRole();
        role.setCategories(cats);
        roleDao.save(role);
        return role;
    }
}
