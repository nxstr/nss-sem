package cz.cvut.fel.nss.chatgc.consumer.kafka;

import cz.cvut.fel.nss.chatgc.dto.MessageDto;

/**
 * Kafka Events Listener
 */

public interface KafkaHandler {

    /**
     * Sets next eventHandler in chain of responsibility.
     * @param handler KafkaHandler that will be next in chain
     */
    void setNext(KafkaHandler handler);

    /**
     * Checks if current handler can handle event, or it has to be passed to next handler in chain.
     * @param dto MessageDto is event data
     */
    void canHandle(MessageDto dto);

    /**
     * Handles and processing event.
     * @param message MessageDto is event data
     */
    void handle(MessageDto message);
}
