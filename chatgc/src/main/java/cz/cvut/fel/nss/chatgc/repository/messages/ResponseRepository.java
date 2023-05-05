package cz.cvut.fel.nss.chatgc.repository.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Response;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends MessageRepository<Response, Integer> {
}
