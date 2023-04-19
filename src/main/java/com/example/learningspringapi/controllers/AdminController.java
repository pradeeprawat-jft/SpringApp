package com.example.learningspringapi.controllers;

import com.example.learningspringapi.entity.User;
import com.example.learningspringapi.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("admin/api")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private MainController mainController;


    //    ****************************************************** register ****************************************

    @PostMapping
    public String register(@ModelAttribute User user, RedirectAttributes alert) {
        String status = adminService.signUp(user);
        if (status.equals("User Already Exist")) {
            alert.addFlashAttribute("error", "Email Already Exist");
            return "redirect:/auth/register";
        }
        mainController.sendMail(user.getEmail());

        return "redirect:/auth/otp?email=" + user.getEmail();
    }


    //    ****************************************************** delete user  ****************************************

    @PostMapping("remove")
    public String remove(@RequestParam("uid") Integer id, RedirectAttributes alert) {
        System.out.println("id is " + id);
        String status = adminService.deleteUser(id);
        if (status.equals("deleted Successfully")) {
            alert.addFlashAttribute("message", "Deleted successfully ");
        }
        return "redirect:/auth/userList";
    }


}
