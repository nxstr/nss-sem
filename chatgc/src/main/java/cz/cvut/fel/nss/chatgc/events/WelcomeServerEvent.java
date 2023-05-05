package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.dto.Client;
import cz.cvut.fel.nss.chatgc.model.users.User;
import lombok.Getter;
import lombok.Setter;

public record WelcomeServerEvent(Client client) implements CommunicationEvent {
@Override
public String getEventType() {
        return "welcome";
        }
        }
