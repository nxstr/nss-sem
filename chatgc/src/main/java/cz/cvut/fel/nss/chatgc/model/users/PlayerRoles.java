package cz.cvut.fel.nss.chatgc.model.users;

/**
 * Player's role.
 */
public enum PlayerRoles {
    REGISTERED("REGISTERED"), GUEST("GUEST");

    private final String name;

    PlayerRoles(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
