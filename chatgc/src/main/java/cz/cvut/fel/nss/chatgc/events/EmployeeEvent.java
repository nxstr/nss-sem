package cz.cvut.fel.nss.chatgc.events;

import cz.cvut.fel.nss.chatgc.dto.EmployeeDTO;

public record EmployeeEvent(String message, EmployeeDTO employee) implements CommunicationEvent{
    @Override
    public String getEventType() {
        return "employee";
    }
}
