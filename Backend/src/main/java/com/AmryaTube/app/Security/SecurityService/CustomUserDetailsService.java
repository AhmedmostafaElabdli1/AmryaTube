package com.AmryaTube.app.Security.SecurityService;

import com.AmryaTube.app.Entity.User;
import com.AmryaTube.app.Enums.GlobalRole;
import com.AmryaTube.app.Repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new UsernameNotFoundException("User With Email " + email + " Not Found"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_"+GlobalRole.VIEWER))
        );



    }
}
