package com.example.learningspringapi.services;

import com.example.learningspringapi.entity.Role;
import com.example.learningspringapi.entity.User;
import com.example.learningspringapi.errorHanders.UserException;
import com.example.learningspringapi.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class AdminService {


    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public String signUp(User u) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (adminRepository.findByEmail(u.getEmail()) != null) {
            return "User Already Exist";
        } else {
            String encodedPassword = encoder.encode(u.getPassword());
            u.setPassword(encodedPassword);
            u.setRole(Role.ADMIN);
            adminRepository.save(u);
        }
        return "User Created";
    }

    public List<User> allUser() {
        return adminRepository.findAll();
    }


    public String signIn(String email, String password) {
        User user = adminRepository.findByEmail(email);
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


    public String deleteUser(Integer id) {
        if (adminRepository.findById(id) != null) {
            adminRepository.deleteById(id);
            return "deleted Successfully";
        }
        return "User Not Found";
    }

    public User otpUpdate(Integer id, User u) throws UserException {
        Optional<User> optionalUser = adminRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setOtp(u.getOtp());
            adminRepository.save(user);
            return u;
        }
        throw new UserException("User Not Found " + u.getId());
    }

    public String userUpdate(Integer id, User u) throws UserException {
        Optional<User> optionalUser = adminRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(u.getName());
            user.setEmail(u.getEmail());
            user.setAge(u.getAge());
            adminRepository.save(user);
            return "success";
        }
        throw new UserException("User Not Found " + u.getId());
    }
}
