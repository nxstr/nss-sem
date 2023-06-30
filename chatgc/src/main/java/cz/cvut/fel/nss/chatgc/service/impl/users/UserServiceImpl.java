package cz.cvut.fel.nss.chatgc.service.impl.users;

import cz.cvut.fel.nss.chatgc.exceptions.ExistsException;
import cz.cvut.fel.nss.chatgc.model.users.User;
import cz.cvut.fel.nss.chatgc.repository.users.UserRepository;
import cz.cvut.fel.nss.chatgc.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;


@Service
@AllArgsConstructor
public abstract class UserServiceImpl<T extends User> implements UserService {

    private final UserRepository<T, Integer> userDao;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder encoder;


    @Transactional
    public void persist(User user) {
        user.encodePassword(encoder);
        userDao.save(user);
    }

    @Transactional
    public void update(User user) {
        userDao.save(user);
    }

    @Transactional
    public User findById(Integer id) {
        return userDao.findById(id).orElse(null);
    }

    @Transactional
    public User findByUsername(String name) {
        return userDao.findByUsername(name);
    }

    @Transactional
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Transactional
    public void changePassword(User user, String password) {
        user.setPassword(password);
        user.encodePassword(encoder);
        userDao.save(user);
    }

    @Transactional
    public void changeEmail(User user, String email) {
        if (findByEmail(email) != null) {
            throw new ExistsException("email already exists");
        }
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (!patternMatches(email, regexPattern)) {
            throw new ExistsException("email is not valid");
        }
        user.setEmail(email);
        userDao.save(user);
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
