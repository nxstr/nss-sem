package cz.cvut.fel.nss.chatgc.model.users;


import cz.cvut.fel.nss.chatgc.model.AbstractEntity;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;


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

    public void encodePassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
