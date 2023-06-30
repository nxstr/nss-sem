package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.model.Chat;

public record ChatEvent(String message, Chat chat) implements CommunicationEvent {

    @Override
    public String getEventType() {
        return "chat";
    }
}
