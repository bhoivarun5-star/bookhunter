package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been successfully logged out.");
        }
        return "login";
    }

    // ─── Register ─────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        // Password match check
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/register";
        }

        // Basic length validation
        if (username.trim().length() < 3) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username must be at least 3 characters.");
            return "redirect:/register";
        }
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 6 characters.");
            return "redirect:/register";
        }

        try {
            userService.registerUser(username.trim(), email.trim(), password);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account created successfully! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/register";
        }
    }

    // ─── Root redirect ────────────────────────────────────────────────────────

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }
}
