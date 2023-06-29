package cz.cvut.fel.nss.chatgc.service.impl.utils;

import cz.cvut.fel.nss.chatgc.security.DefaultAuthenticationProvider;
import cz.cvut.fel.nss.chatgc.security.service.AccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Represents Login Service.
 */
@Service
public class LoginService {

    private final DefaultAuthenticationProvider provider;
    private final AccountDetailsService userDetailsService;

    @Autowired
    public LoginService(DefaultAuthenticationProvider provider, AccountDetailsService userDetailsService) {
        this.provider = provider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Provides user authentication.
     * @param username username from client request
     * @param password raw password from client request
     */
    @Transactional
    public void loginUser (String username, String password){

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        provider.authenticate(authentication);

    }
}

