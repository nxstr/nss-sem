package cz.cvut.fel.nss.chatgc.dto;

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
}
