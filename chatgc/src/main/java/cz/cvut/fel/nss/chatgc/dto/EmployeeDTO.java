package cz.cvut.fel.nss.chatgc.dto;

import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeDTO {
    String username;
    String password;
    String email;
    Integer roleId;
    String roleName;
    Integer id;

    public Employee accept(Visitor v) {
        return v.visitEmployeeDto(this);
    }
}
