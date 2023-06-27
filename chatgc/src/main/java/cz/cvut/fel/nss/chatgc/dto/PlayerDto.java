package cz.cvut.fel.nss.chatgc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlayerDto {
    String username;
    String email;
    String password;
    Integer id;
}
