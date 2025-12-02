package com.project.codelight.user.service;

import com.project.codelight.user.domain.User;
import com.project.codelight.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
