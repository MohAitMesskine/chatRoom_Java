package com.example.chatRoom.service;

import com.example.chatRoom.model.User;
import com.example.chatRoom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        User isExist = userRepository.findByUsername(user.getUsername());
        if (isExist != null) {
            System.out.println("User already exists");
            return;
        }
        userRepository.save(user);

    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    public User authenticateUsr(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            //System.out.println("hhhhhhhhhhhhh1222226666 " +user.getUsername());
            return user;
        }
        return null;
    }

    public boolean usernameExists(String username) {
        User user = userRepository.findByUsername(username);
        return user != null;
    }
    public boolean emailExists(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }
}
