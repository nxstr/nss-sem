package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.users.User;

public interface UserService {
    void persist(User user);

    void update(User user);

    User findById(Integer id);

    User findByUsername(String name);

    User findByEmail(String email);

    void changePassword(User user, String password);

    void changeEmail(User user, String email);
}
