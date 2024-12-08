package com.example.chatRoom.controller;

import com.example.chatRoom.dto.UserLoginDto;
import com.example.chatRoom.dto.UserRegistrationDto;
import com.example.chatRoom.model.User;
import com.example.chatRoom.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService, HttpSession httpSession) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(HttpSession httpSession) {
        // Add authentication check
        if (!isAuthenticated(httpSession)) {
            return "redirect:/login";
        }
        return "home";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("userRegistrationDto")) {
            model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDto registrationDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (userService.usernameExists(registrationDto.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username already taken");
        }

        if (userService.emailExists(registrationDto.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email already registered");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationDto", bindingResult);
            redirectAttributes.addFlashAttribute("userRegistrationDto", registrationDto);
            return "redirect:/register";
        }

        try {
            User user = new User();
            user.setUsername(registrationDto.getUsername());
            user.setEmail(registrationDto.getEmail());
            user.setPassword(registrationDto.getPassword()); // Ensure password is hashed in service layer
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Please try again.");
            return "redirect:/register";
        }
    }

    @GetMapping({"/", "/login"})
    public String login(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(UserLoginDto loginDto, HttpSession httpSession){
        try {
            User user = userService.authenticateUsr(loginDto.getUsername(), loginDto.getPassword());
            if (user != null) {
                // Implement session management here tal mn ba3d
                System.out.println("User logged in successfully");
                httpSession.setAttribute("username", user.getUsername());
                return "redirect:/home";
            }
        } catch (Exception e) {
            System.out.println("Error in loginUser: " + e.getMessage());
        }

        return "redirect:/login?error";
    }

    // Add this method to check if user is authenticated
    private boolean isAuthenticated(HttpSession httpSession) {
        // Implement your authentication check logic here
        return httpSession.getAttribute("username") != null;
    }
}