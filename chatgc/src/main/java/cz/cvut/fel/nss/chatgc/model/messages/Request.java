package cz.cvut.fel.nss.chatgc.model.messages;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
@DiscriminatorValue(value="REQUEST")
public class Request extends Message{

    @ManyToMany
    private Set<Category> categories;

    public Request(String content, LocalDateTime date, Chat chat, MessageType type, Set<Category> categories) {
        super(content, date, chat, type);
        this.categories = categories;
    }
}
