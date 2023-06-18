package cz.cvut.fel.nss.chatgc.model.messages;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import cz.cvut.fel.nss.chatgc.model.AbstractEntity;
import cz.cvut.fel.nss.chatgc.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "MessageType",
        discriminatorType = DiscriminatorType.STRING)
public abstract class Message extends AbstractEntity {
    private String dataPath;
    private LocalDateTime date;

    @ManyToOne(optional = false)
    private Chat chat;


    private MessageType type;

}
