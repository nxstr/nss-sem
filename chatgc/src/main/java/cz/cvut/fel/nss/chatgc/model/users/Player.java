package cz.cvut.fel.nss.chatgc.model.users;


import cz.cvut.fel.nss.chatgc.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
@DiscriminatorValue(value="PLAYER")
public class Player extends User{

    @OneToOne
    private Chat chat;

    @Column(name = "PlayerRole")
    private PlayerRoles role;

    public Player(String username, String email, String password, PlayerRoles role) {
        super(username, email, password);
        this.role = role;
    }
}
