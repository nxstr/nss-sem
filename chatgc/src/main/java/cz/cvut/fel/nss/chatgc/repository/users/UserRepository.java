package cz.cvut.fel.nss.chatgc.repository.users;


import cz.cvut.fel.nss.chatgc.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository<T, Integer> extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    User findByEmail(String email);
}
