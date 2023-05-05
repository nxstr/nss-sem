package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.dto.ChatRequestDto;
import cz.cvut.fel.nss.chatgc.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message")
    @ResponseBody
    public void sendMessage(@RequestBody ChatRequestDto messageDto) {
        chatService.broadcast(messageDto);

        //publisher
    }
}
