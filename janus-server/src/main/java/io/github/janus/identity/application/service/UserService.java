package io.github.janus.identity.application.service;

import io.github.janus.identity.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private Argon2PasswordEncoder encoder;
    private UserJpaRepository userJpaRepository;

    public UserService(Argon2PasswordEncoder encoder, UserJpaRepository userJpaRepository) {
        this.encoder = encoder;
        this.userJpaRepository = userJpaRepository;
    }



}
