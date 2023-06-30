package cz.cvut.fel.nss.chatgc.model.users;


import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.nss.chatgc.dto.PlayerDto;
import cz.cvut.fel.nss.chatgc.mapper.Visitor;
import cz.cvut.fel.nss.chatgc.model.Chat;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

/**
 * Concrete User class representing Player entity.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
@DiscriminatorValue(value = "PLAYER")
public class Player extends User {

    @OneToOne
    @JsonIgnore
    @Getter
    @Setter
    private Chat chat;

    @Column(name = "PlayerRole")
    @Getter
    @Setter
    private PlayerRoles role;

    public Player(String username, String email, String password, PlayerRoles role) {
        super(username, email, password);
        this.role = role;
    }

    public PlayerDto accept(Visitor v) {
        return v.visitPlayerEntity(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return role == player.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }
}
