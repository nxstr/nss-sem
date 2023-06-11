package cz.cvut.fel.nss.chatgc.security;
import cz.cvut.fel.nss.chatgc.security.model.AccountDetails;
import cz.cvut.fel.nss.chatgc.security.model.AuthenticationToken;
import cz.cvut.fel.nss.chatgc.security.service.AccountDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAuthenticationProvider.class);

    private final AccountDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultAuthenticationProvider(AccountDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getPrincipal().toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Authenticating user {}", username);
        }

        final AccountDetails userDetails = (AccountDetails) userDetailsService.loadUserByUsername(username);
        final String password = (String) authentication.getCredentials();

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Provided credentials don't match.");
        }
        AuthenticationToken token = SecurityUtils.setCurrentUser(userDetails);
        System.out.println(token.getPrincipal().getUsername() + "loggeddddddddddddddddddddddddddd");
        return token;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass) ||
                AuthenticationToken.class.isAssignableFrom(aClass);
    }
}


