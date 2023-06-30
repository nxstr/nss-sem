package cz.cvut.fel.nss.chatgc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "folders")
public class Folder extends AbstractEntity {
    private String name;

    @ManyToMany
    private Set<Chat> chats;
}
