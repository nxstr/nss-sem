package cz.cvut.fel.nss.chatgc.consumer.kafka;

import cz.cvut.fel.nss.chatgc.dto.MessageDto;

public interface KafkaHandler {
    void setNext(KafkaHandler handler);

    void canHandle(MessageDto dto);

    void handle(MessageDto message);
}
