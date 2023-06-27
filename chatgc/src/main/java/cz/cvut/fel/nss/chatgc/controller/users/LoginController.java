package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.impl.utils.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;
    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final SimpUserRegistry simpUserRegistry;

    @Autowired
    public LoginController(LoginService loginService, SimpUserRegistry simpUserRegistry) {
        this.loginService = loginService;
        this.simpUserRegistry = simpUserRegistry;
    }

    @PostMapping(value = "/api/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity loginUser(@RequestBody HashMap<String, String> request){
        try {
            loginService.loginUser(request.get("username"), request.get("password"));

            LOG.trace("User {} successfully logged in", request.get("username"));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (NullPointerException | BadCredentialsException e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/api/logout")
    public ResponseEntity logoutUser(Principal principal){
        try {

            final AuthenticationToken auth = (AuthenticationToken) principal;
            String name = auth.getPrincipal().getAccount().getUsername();
//            SecurityUtils.setCurrentUser(null);
//            SecurityContext context = SecurityContextHolder.getContext();
//            SecurityContextHolder.clearContext();
//            context.setAuthentication(null);
            MessageDto m = new MessageDto("logout", name);
            m.setContent("logout-action");
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_LOGOUT, m);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (NullPointerException | BadCredentialsException e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping(value = "/api/log", consumes = "application/json", produces = "application/json")
    public void loginMessage(@RequestBody MessageDto m) {
        try {
            m.setContent("logged-in-action/" + m.getContent());
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_LOGIN, m).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}



