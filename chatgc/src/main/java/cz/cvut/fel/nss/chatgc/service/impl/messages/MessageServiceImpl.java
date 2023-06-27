package cz.cvut.fel.nss.chatgc.service.impl.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.repository.messages.MessageRepository;
import cz.cvut.fel.nss.chatgc.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public abstract class MessageServiceImpl<T extends Message> implements MessageService {
    private final MessageRepository<T, Integer> messageDao;

    @Transactional
    public Message persist(Message message){
        messageDao.save(message);
        return message;
    }

    @Transactional
    public void update(Message message){
        messageDao.save(message);
    }

    @Transactional
    public Message findById(Integer id){
        return messageDao.findById(id).orElse(null);
    }

    public void delete(Message message){
        messageDao.delete(message);
    }

}
