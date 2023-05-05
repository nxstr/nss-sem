package cz.cvut.fel.nss.chatgc.service.messages;

import cz.cvut.fel.nss.chatgc.model.messages.Message;
import cz.cvut.fel.nss.chatgc.repository.messages.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public abstract class MessageService<T extends Message> {
    private final MessageRepository<T, Integer> messageDao;

    @Transactional
    public Message persist(Message message){
        messageDao.save(message);
        return message;
    }

    @Transactional
    public Message findById(Integer id){
        return messageDao.findById(id).orElse(null);
    }

}
