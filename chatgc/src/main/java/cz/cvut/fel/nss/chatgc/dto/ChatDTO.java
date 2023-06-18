package cz.cvut.fel.nss.chatgc.dto;

import cz.cvut.fel.nss.chatgc.model.Category;
import cz.cvut.fel.nss.chatgc.model.Folder;
import cz.cvut.fel.nss.chatgc.model.messages.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class ChatDTO {
    @Getter
    @Setter
    private boolean isOpen;
    @Getter
    @Setter
    private String playerUsername;
    @Getter
    @Setter
    private Integer id;
    @Getter
    @Setter
    private Set<Category> categories;
    @Getter
    @Setter
    private Set<Folder> folders;
    @Getter
    @Setter
    private MessageDto lastMessage;

    public ChatDTO(boolean isOpen, String playerUsername, Integer id, Set<Category> categories, Set<Folder> folders, MessageDto lastMessage) {
        this.isOpen = isOpen;
        this.playerUsername = playerUsername;
        this.id = id;
        this.categories = categories;
        this.folders = folders;
        this.lastMessage = lastMessage;
    }

    public ChatDTO() {
    }
}
