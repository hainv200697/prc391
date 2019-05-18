package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public Page<User> findAllUser(String search, int page, int size) {
        Pageable sortedByCreatedDateDesc =
                PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> result = userRepository.findAll(where(UserRepository.filterByName(search)), sortedByCreatedDateDesc);
        return result;
    }


    public User createUser(User user) {
        Optional<User> duplicateUser = userRepository.findByName(user.getName());
        if (duplicateUser.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "This account exist!");
        } else {
            user.setRole("ROLE_ADMIN");
            user.setActive(true);
            user.setPassword(encoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
    }

    public User getUserByUserName(String name) {
        Optional<User> existUser = userRepository.findByName(name);
        if (existUser.isPresent()) {
            return userRepository.findByName(name).get();
        }
        return null;
    }
}
