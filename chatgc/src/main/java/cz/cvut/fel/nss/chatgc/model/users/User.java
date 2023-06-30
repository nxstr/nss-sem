package cz.cvut.fel.nss.chatgc.model.users;


import cz.cvut.fel.nss.chatgc.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

/**
 * Abstract user entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "UserType",
        discriminatorType = DiscriminatorType.STRING)

public abstract class User extends AbstractEntity {

    private String username;
    private String email;
    private String password;

    /**
     * Encode raw password.
     *
     * @param passwordEncoder
     */

    public void encodePassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

    /**
     * Reset password.
     */
    public void erasePassword() {
        this.password = null;
    }

}
