package com.example.learningspringapi.controllers;

import com.example.learningspringapi.entity.User;
import com.example.learningspringapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("users/api")
public class UserController {


    @Autowired
    private MainController mainController;

    @Autowired
    private UserService userService;

    @PostMapping
    public String register(@ModelAttribute User user, RedirectAttributes alert) {
        String status = userService.signUp(user);
        if (status.equals("User Already Exist")) {
            alert.addFlashAttribute("error", "Email Already Exist");
            return "redirect:/auth/register";
        }
        mainController.sendMail(user.getEmail());
        return "redirect:/auth/otp?email=" + user.getEmail();
    }
}