package com.example.learningspringapi.controllers;

import com.example.learningspringapi.entity.EmailDetails;
import com.example.learningspringapi.entity.Role;
import com.example.learningspringapi.entity.User;
import com.example.learningspringapi.errorHanders.UserException;
import com.example.learningspringapi.repository.AdminRepository;
import com.example.learningspringapi.services.AdminService;
import com.example.learningspringapi.services.EmailServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

@Controller
@RequestMapping("auth")
public class MainController {

    @Autowired
    private final EmailServiceImpl emailService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private AdminRepository repo;

    public MainController(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/index")
    public String loginPage() {
        return "index";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/admin/register")
    public String adminregisterPage() {
        return "admin_register";
    }

    @GetMapping("/login")
    public String index() {
        return "login";
    }

    @GetMapping("/otp")
    public String otp() {
        return "otp";
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/userList")
    public String users(Model model) {
        model.addAttribute("user", adminService.allUser());
        return "all_users";
    }

    //    ****************************************************** Update User  ****************************************

    public void updateUser(Integer id, User user) throws UserException {
        adminService.otpUpdate(id, user);
    }

    //    ****************************************************** Otp verification  ****************************************

    @PostMapping("/otp")
    public String otpVerification(@RequestParam("otp") String otp, @RequestParam("email") String email, RedirectAttributes alert) throws UserException {
        if (httpSession.getAttribute("OTP").equals(otp) && httpSession.getAttribute("OTP") != null) {
            User user = repo.findByEmail(email);
            System.out.println(user.getEmail());
            if (user.getOtp() == null) {
                user.setOtp("validate");
                System.out.println("chaining....");
                Integer uid = user.getId();
                updateUser(uid, user);
            }
            alert.addFlashAttribute("success", "OTP Validated successfully !");
            return "redirect:/auth/login";
        }
        alert.addFlashAttribute("message", "Wrong Otp try Again !");
        return "redirect:/auth/otp";
    }

//       ****************************************************** Logout ****************************************************


    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/auth/index";
    }

    //       ****************************************************** Login ****************************************************


    @PostMapping("/signIn")
    public String login(@ModelAttribute User request, RedirectAttributes alert) {
        String status = adminService.signIn(request.getEmail(), request.getPassword());
        System.out.println(status);
        switch (status) {
            case "wrong password" -> {
                alert.addFlashAttribute("error", "Wrong Password");
                return "redirect:/auth/login";
            }
            case "email not exist" -> {
                alert.addFlashAttribute("error", "User does no Exist");
                return "redirect:/auth/login";
            }
            case "user is not validate" -> {
                alert.addFlashAttribute("message", "First validate your Gmail ID");
                return "redirect:/auth/otp?email=" + request.getEmail();
            }
        }
        User u = repo.findByEmail(request.getEmail());
        httpSession.setAttribute("username", u.getEmail());
        httpSession.setAttribute("role", u.getRole().equals(Role.ADMIN) ? "ADMIN" : "USER");
        alert.addFlashAttribute("user", " Welcome " + u.getName());
        return "redirect:/auth/index";
    }

    public void
    sendMail(String email) {
        Random random = new Random();
        String id = String.format("%04d", random.nextInt(1000000));
        httpSession.setAttribute("OTP", id);
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(email);
        emailDetails.setSubject("Confirmation mail");
        emailDetails.setMsgBody("hey\nThis is just a confirmation mail and Your OTP is\n" + id + "\nthanks");
        emailService.sendSimpleMail(emailDetails);
    }

    public String sendMailWithAttachment(
            EmailDetails details) {
        return emailService.sendMailWithAttachment(details);
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/editProfile")
    public String editProfile(@RequestParam("email") String email, Model model) {
        User user = repo.findByEmail(email);
        model.addAttribute("currentUser", user);
        return "editProfile";
    }

//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/edit")
    public String edit(@ModelAttribute User request, RedirectAttributes alert) throws UserException {
        Integer id = request.getId();
        String status = adminService.userUpdate(id, request);
        if (status.equals("success")) {
            alert.addFlashAttribute("message", "profile Updated Successfully");
        }
        return "redirect:/auth/editProfile?email="+request.getEmail();
    }

}
