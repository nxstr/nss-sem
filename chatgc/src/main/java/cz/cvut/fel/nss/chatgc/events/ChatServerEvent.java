package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.model.users.User;

import java.util.Set;

public record ChatServerEvent(String message,
                              String userName, Set<Client> registeredClients)
        implements CommunicationEvent {

    @Override
    public String getEventType() {
        return "chat";
    }
}
