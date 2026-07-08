package io.github.janus.identity;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private Argon2PasswordEncoder encoder;
    private UserRepository userRepository;

    public UserService(Argon2PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }



}
