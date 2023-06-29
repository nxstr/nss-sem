package cz.cvut.fel.nss.chatgc.repository.users;


import cz.cvut.fel.nss.chatgc.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository<T, Integer> extends JpaRepository<User, Integer> {

    /**
     * Finds User by username.
     * @param username means unique username of user
     * @return User
     */

    User findByUsername(String username);

    /**
     * Finds User by email address.
     * @param email means unique email of user
     * @return User
     */

    User findByEmail(String email);
}
