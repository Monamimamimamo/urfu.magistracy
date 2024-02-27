package urfumagistracy.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import urfumagistracy.backend.security.UserDetailsImpl;
import urfumagistracy.backend.security.UserRepo;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                "$User {username} not found"
        ));
        return UserDetailsImpl.build(user);
    }
}
