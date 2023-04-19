package com.example.learningspringapi.services;

import com.example.learningspringapi.entity.Role;
import com.example.learningspringapi.entity.User;
import com.example.learningspringapi.errorHanders.UserException;
import com.example.learningspringapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    protected UserRepository userRepo;

    public String signUp(User u) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (userRepo.findByEmail(u.getEmail()) != null) {
            return "User Already Exist";
        } else {
            String encodedPassword = encoder.encode(u.getPassword());
            u.setPassword(encodedPassword);
            u.setRole(Role.USER);
            userRepo.save(u);
        }
        return "User Created";
    }


    public String signIn(String email, String password) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            if (Objects.equals(user.getOtp(), "validate")) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(password, user.getPassword())) {
                    return "Success";
                } else {
                    return "wrong password";
                }
            } else {
                return "user is not validate";
            }
        }
        return "email not exist";
    }


    public User userUpdate(Integer id, User u) throws UserException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setOtp(u.getOtp());
            userRepo.save(user);
            return u;
        }
        throw new UserException("User Not Found " + u.getId());
    }
}
