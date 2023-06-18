package cz.cvut.fel.nss.chatgc.service.users;


import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.events.WelcomeServerEvent;
import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.model.users.User;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public abstract class UserService<T extends User> {

    private final UserRepository<T, Integer> userDao;
    private final Set<Client> registeredClients = new HashSet<>();
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder encoder;



    @Transactional
    public void persist(T user){
        user.encodePassword(encoder);
        userDao.save(user);
        System.out.println("saved");
    }

    @Transactional
    public void update(T user){
        userDao.save(user);
        System.out.println("updates");
    }

    @Transactional
    public User findById(Integer id){
        return userDao.findById(id).orElse(null);
    }

    @Transactional
    public User findByUsername(String name){
        return userDao.findByUsername(name);
    }

    @Transactional
    public User findByEmail(String email){
        return userDao.findByEmail(email);
    }

    @Transactional
    public void changePassword(User user, String password){
        user.setPassword(password);
        user.encodePassword(encoder);
        userDao.save(user);
    }

    @Transactional
    public void changeEmail(User user, String email){
        if(findByEmail(email)!=null){
            throw new ExistsException("email already exists");
        }
        user.setEmail(email);
        userDao.save(user);
    }
}
