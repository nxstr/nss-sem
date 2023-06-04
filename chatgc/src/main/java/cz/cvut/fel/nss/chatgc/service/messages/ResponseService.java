package cz.cvut.fel.nss.chatgc.service.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Response;
import cz.cvut.fel.nss.chatgc.repository.messages.MessageRepository;
import cz.cvut.fel.nss.chatgc.repository.messages.ResponseRepository;
import org.springframework.stereotype.Service;

@Service
public class ResponseService extends MessageService<Response> {

    private final ResponseRepository responseRepository;
    public ResponseService(MessageRepository<Response, Integer> messageDao, ResponseRepository responseRepository) {
        super(messageDao);

        this.responseRepository = responseRepository;
    }
}
