package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.model.Category;

public record CategoryEvent(String message, Category category) implements CommunicationEvent {
    @Override
    public String getEventType() {
        return "category";
    }
}
