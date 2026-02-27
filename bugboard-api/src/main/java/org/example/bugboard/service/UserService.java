package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.entity.User;
import org.example.bugboard.exception.ResourceNotFoundException;
import org.example.bugboard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User update(Long id, String nickname) {
        User user = findById(id);
        user.update(nickname);
        return user;
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        user.softDelete();
    }
}
