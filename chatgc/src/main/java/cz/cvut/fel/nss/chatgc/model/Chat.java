package cz.cvut.fel.nss.chatgc.model;

import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat extends AbstractEntity{
    private boolean isOpen;

    @OneToOne(mappedBy = "chat", optional = false)
    private Player player;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    @ManyToMany
    private Set<Category> categories;

    @ManyToMany(mappedBy = "chats")
    private Set<Folder> folders;

    private String playerUsername;


}
