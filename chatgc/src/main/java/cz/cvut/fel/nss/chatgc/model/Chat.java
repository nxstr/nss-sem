package cz.cvut.fel.nss.chatgc.model;

import com.fasterxml.jackson.annotation.*;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Representing Chat entity.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat extends AbstractEntity {
    @JsonIgnore
    private boolean isOpen;

    @OneToOne(mappedBy = "chat", optional = false)
    @JsonIgnore
    private Player player;

    @OneToMany(mappedBy = "chat")
    @JsonIgnore
    private ArrayList<Message> messages;

    @ManyToMany
    @JsonIgnore
    private Set<Category> categories;

    @ManyToMany(mappedBy = "chats")
    @JsonIgnore
    private Set<Folder> folders;

    private String playerUsername;


}
