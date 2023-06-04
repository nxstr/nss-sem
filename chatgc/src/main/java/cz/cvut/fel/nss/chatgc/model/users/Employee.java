package cz.cvut.fel.nss.chatgc.model.users;

import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employees")
@DiscriminatorValue(value="EMPLOYEE")
public class Employee extends User{

    @ManyToOne(optional = false)
    private Role role;

    @OneToMany(mappedBy = "employee")
    private List<Response> responses;

    public Employee(String username, String email, String password, Role role) {
        super(username, email, password);
        this.role = role;
    }
}
