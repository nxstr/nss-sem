package cz.cvut.fel.nss.chatgc.model.messages;

/**
 * Types for Message entity.
 */
public enum MessageType {
    TEXT("TEXT"), FILE("FILE");

    private final String name;

    MessageType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
