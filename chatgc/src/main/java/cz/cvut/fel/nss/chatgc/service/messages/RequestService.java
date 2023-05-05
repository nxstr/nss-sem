package cz.cvut.fel.nss.chatgc.service.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.repository.messages.MessageRepository;
import cz.cvut.fel.nss.chatgc.repository.messages.RequestRepository;
import org.springframework.stereotype.Service;

@Service
public class RequestService extends MessageService<Request> {

    private final RequestRepository requestDao;

    public RequestService(MessageRepository<Request, Integer> messageDao, RequestRepository requestDao) {
        super(messageDao);
        this.requestDao = requestDao;
    }

}
