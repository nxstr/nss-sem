package cz.cvut.fel.nss.chatgc.repository.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Request;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends MessageRepository<Request, Integer> {
}
