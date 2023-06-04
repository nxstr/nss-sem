package cz.cvut.fel.nss.chatgc.controller;

import cz.cvut.fel.nss.chatgc.service.users.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/register-client/{name}")
    public SseEmitter sseEmitter(@PathVariable String name) {
        return playerService.registerClient(name);
    }

}
