package org.example.bugboard.service;

import lombok.RequiredArgsConstructor;
import org.example.bugboard.entity.Users;
import org.example.bugboard.exception.ResourceNotFoundException;
import org.example.bugboard.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;

    @Transactional
    public Users create(Users users) {
        return usersRepository.save(users);
    }

    public Users findById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    @Transactional
    public Users update(Long id, String nickname) {
        Users users = findById(id);
        users.update(nickname);
        return users;
    }

    @Transactional
    public void delete(Long id) {
        Users users = findById(id);
        users.softDelete();
    }
}
