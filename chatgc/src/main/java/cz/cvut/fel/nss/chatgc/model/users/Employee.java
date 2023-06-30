package cz.cvut.fel.nss.chatgc.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.nss.chatgc.dto.EmployeeDTO;
import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.Role;
import cz.cvut.fel.nss.chatgc.model.messages.Response;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * Concrete User class representing Employee entity.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employees")
@DiscriminatorValue(value = "EMPLOYEE")
public class Employee extends User {

    @ManyToOne(optional = false)
    @Getter
    @Setter
    private Role role;

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    @Getter
    @Setter
    private List<Response> responses;

    public Employee(String username, String email, String password, Role role) {
        super(username, email, password);
        this.role = role;
    }

    public EmployeeDTO accept(Visitor v) {
        return v.visitEmployeeEntity(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return role.equals(employee.role) && Objects.equals(responses, employee.responses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }
}
