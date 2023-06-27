package cz.cvut.fel.nss.chatgc.service.impl.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.repository.messages.MessageRepository;
import cz.cvut.fel.nss.chatgc.repository.messages.ResponseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("response")
public class ResponseServiceImpl extends MessageServiceImpl<Response> {

    private final ResponseRepository responseRepository;
    public ResponseServiceImpl(MessageRepository<Response, Integer> messageDao, ResponseRepository responseRepository) {
        super(messageDao);

        this.responseRepository = responseRepository;
    }
}
