package cz.cvut.fel.nss.chatgc.dto;

import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.users.Player;
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

    public Player accept(Visitor v){
        return v.visitPlayerDto(this);
    }
}
