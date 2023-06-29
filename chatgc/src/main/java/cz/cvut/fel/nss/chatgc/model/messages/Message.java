package cz.cvut.fel.nss.chatgc.model.messages;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import cz.cvut.fel.nss.chatgc.model.AbstractEntity;
import cz.cvut.fel.nss.chatgc.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Abstract Message entity.
 */
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

    public Message(MessageBuilder<?> builder) {
        this.dataPath = builder.dataPath;
        this.date = builder.date;
        this.chat = builder.chat;
        this.type = builder.type;
    }

    /**
     * Abstract Message Builder.
     * @param <T> RequestBuilder or ResponseBuilder
     */

    abstract static class MessageBuilder<T extends MessageBuilder> {
        private String dataPath;
        private LocalDateTime date;
        private Chat chat;
        private MessageType type;

        abstract Message build();

        public T addDataPath(String dataPath){
            this.dataPath = dataPath;
            return self();
        }

        public T addDate(LocalDateTime date){
            this.date = date;
            return self();
        }

        public T addChat(Chat chat){
            this.chat = chat;
            return self();
        }

        public T addType(MessageType type){
            this.type = type;
            return self();
        }

        protected abstract T self();
    }

}
