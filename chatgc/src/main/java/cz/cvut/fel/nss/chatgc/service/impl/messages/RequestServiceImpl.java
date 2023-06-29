package cz.cvut.fel.nss.chatgc.service.impl.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Request;
import cz.cvut.fel.nss.chatgc.repository.messages.MessageRepository;
import cz.cvut.fel.nss.chatgc.repository.messages.RequestRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("request")
public class RequestServiceImpl extends MessageServiceImpl<Request> {

    private final RequestRepository requestDao;

    public RequestServiceImpl(MessageRepository<Request, Integer> messageDao, RequestRepository requestDao) {
        super(messageDao);
        this.requestDao = requestDao;
    }

}
