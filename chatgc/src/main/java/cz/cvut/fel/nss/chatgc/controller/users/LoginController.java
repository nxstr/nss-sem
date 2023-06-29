package cz.cvut.fel.nss.chatgc.controller.users;

import cz.cvut.fel.nss.chatgc.constants.KafkaConstants;
import cz.cvut.fel.nss.chatgc.dto.MessageDto;
import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.service.impl.utils.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@RestController
public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;
    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Login user into system.
     * @param request HashMap<String, String> represents username and raw password
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "/api/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginUser(@RequestBody HashMap<String, String> request){
        try {
            loginService.loginUser(request.get("username"), request.get("password"));
            LOG.info("User {} successfully logged in", request.get("username"));
            return new ResponseEntity<>("", HttpStatus.OK);
        }catch (AccountException | BadCredentialsException e){
            LOG.info(e.getMessage() + ": {} ", request.get("username"));
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Logout authorizes user.
     * @param principal Principal
     * @return ResponseEntity<String>
     */
    @GetMapping(value = "/api/logout")
    public ResponseEntity<String> logoutUser(Principal principal){
        try {
            final AuthenticationToken auth = (AuthenticationToken) principal;
            if(auth==null){
                return new ResponseEntity<>(HttpStatus.OK);
            }
            String name = auth.getPrincipal().getAccount().getUsername();
            MessageDto m = new MessageDto("logout", name);
            m.setContent("logout-action");
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_LOGOUT, m);
            LOG.info("User {} successfully logged out", name);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (NullPointerException | BadCredentialsException e){
            LOG.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Sends "system" login-message.
     * @param m MessageDto
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "/api/log", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> loginMessage(@RequestBody MessageDto m) {
        try {
            m.setContent("logged-in-action/" + m.getContent());
            kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC_LOGIN, m).get();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}



