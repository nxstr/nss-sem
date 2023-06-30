package cz.cvut.fel.nss.chatgc.security.model;

import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import cz.cvut.fel.nss.chatgc.model.users.Employee;
import cz.cvut.fel.nss.chatgc.model.users.Player;
import cz.cvut.fel.nss.chatgc.model.users.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.DiscriminatorValue;
import java.util.*;

public class AccountDetails implements org.springframework.security.core.userdetails.UserDetails {

    private final User user;

    private final Set<GrantedAuthority> authorities;

    public AccountDetails(User user) {
        if (user == null) {
            throw new AccountException("username does not exist");
        }
        this.user = user;
        this.authorities = new HashSet<>();
        addUserRole();
    }

    private void addUserRole() {
        authorities.add(new SimpleGrantedAuthority(user.getClass().getAnnotation(DiscriminatorValue.class).value()));
        if (user.getClass().getAnnotation(DiscriminatorValue.class).value().equals("EMPLOYEE")) {
            Employee employee = (Employee) user;
            authorities.add(new SimpleGrantedAuthority(employee.getRole().getName()));
        } else if (user.getClass().getAnnotation(DiscriminatorValue.class).value().equals("PLAYER")) {
            Player player = (Player) user;
            authorities.add(new SimpleGrantedAuthority(player.getRole().toString()));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getAccount() {
        return user;
    }


}


