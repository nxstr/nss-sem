package cz.cvut.fel.nss.chatgc.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDto {
    String messageType;
    String sender;
    String content;
    String chat;
    List<CategoryDto> categories;
    LocalDateTime date;

    public MessageDto() {
    }

    public MessageDto(String messType, String sender) {
        this.messageType = messType;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public List<CategoryDto> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDto> categories) {
        this.categories = categories;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType='" + messageType + '\'' +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + chat + '\'' +
                '}';
    }
}
