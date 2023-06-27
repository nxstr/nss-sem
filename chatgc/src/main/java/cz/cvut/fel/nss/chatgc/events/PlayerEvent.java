package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.dto.PlayerDto;

public record PlayerEvent(String message, PlayerDto dto) implements CommunicationEvent{

    @Override
    public String getEventType() {
        return "player";
    }
}
