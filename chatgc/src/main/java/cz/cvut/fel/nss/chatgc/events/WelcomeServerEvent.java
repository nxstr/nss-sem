package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.dto.Client;

public record WelcomeServerEvent(Client client) implements CommunicationEvent {
@Override
public String getEventType() {
        return "welcome";
        }
        }
