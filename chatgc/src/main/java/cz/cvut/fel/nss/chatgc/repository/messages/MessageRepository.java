package cz.cvut.fel.nss.chatgc.repository.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository<T, Integer> extends JpaRepository<Message, Integer> {
}