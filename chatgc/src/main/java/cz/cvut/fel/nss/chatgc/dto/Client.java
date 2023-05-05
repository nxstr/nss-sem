package cz.cvut.fel.nss.chatgc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@AllArgsConstructor
public class Client{
    @Getter
    @Setter
    private SseEmitter sseEmitter;
    @Getter
    @Setter
    private String name;
}
