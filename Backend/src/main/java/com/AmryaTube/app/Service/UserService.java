package com.AmryaTube.app.Service;

import com.AmryaTube.app.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



}
