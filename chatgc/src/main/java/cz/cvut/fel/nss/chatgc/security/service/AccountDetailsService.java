package cz.cvut.fel.nss.chatgc.security.service;

import cz.cvut.fel.nss.chatgc.security.model.AccountDetails;
import cz.cvut.fel.nss.chatgc.service.impl.users.EmployeeServiceImpl;
import cz.cvut.fel.nss.chatgc.service.impl.users.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final PlayerServiceImpl userDao;
    private final EmployeeServiceImpl adminDao;

    @Autowired
    public AccountDetailsService(PlayerServiceImpl userDao, EmployeeServiceImpl adminDao) {
        this.userDao = userDao;
        this.adminDao = adminDao;
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (adminDao.findByUsername(username) != null) {
            return new AccountDetails(adminDao.findByUsername(username));
        }else {
            return new AccountDetails(userDao.findByUsername(username));
        }
    }


}

