package cz.cvut.fel.nss.chatgc.service;

import cz.cvut.fel.nss.chatgc.model.users.User;

/**
 * Represents User Service.
 */

public interface UserService {

    /**
     * Creates new user.
     *
     * @param user User entity that will be saved
     */
    void persist(User user);

    /**
     * Updates user.
     *
     * @param user User entity that will be updated
     */
    void update(User user);

    /**
     * Finds user by its id.
     *
     * @param id Integer id of user
     * @return User
     */
    User findById(Integer id);

    /**
     * Finds user by its username.
     *
     * @param name String username of user
     * @return User
     */
    User findByUsername(String name);

    /**
     * Finds user by its email.
     *
     * @param email String represents email of user
     * @return User
     */
    User findByEmail(String email);

    /**
     * Changes user's password.
     *
     * @param user     User entity that will be updated
     * @param password String new raw password of user
     */
    void changePassword(User user, String password);

    /**
     * Changes user's email address.
     *
     * @param user  User entity that will be updated
     * @param email String new email of user
     */
    void changeEmail(User user, String email);
}
