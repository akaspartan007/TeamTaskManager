package com.ayush.taskmanager.controller;

import java.util.Map;
import com.ayush.taskmanager.dto.SignupRequest;
import com.ayush.taskmanager.entity.Role;
import com.ayush.taskmanager.entity.User;
import com.ayush.taskmanager.repository.UserRepository;
import com.ayush.taskmanager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.secret:DEFAULT_SECRET}")
    private String adminSecret;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {

        // ✅ EMAIL VALIDATION
        if (req.getEmail() == null ||
                !req.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }
        //  PASSWORD VALIDATION
        String password = req.getPassword();

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password required");
        }

        //  block all same digits like 000000
        if (password.matches("^(.)\\1+$")) {
            return ResponseEntity.badRequest().body("Password too weak");
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            return ResponseEntity.badRequest().body("Must contain uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            return ResponseEntity.badRequest().body("Must contain lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            return ResponseEntity.badRequest().body("Must contain number");
        }

        if (!password.matches(".*[@#$%^&+=!].*")) {
            return ResponseEntity.badRequest().body("Must contain special character");
        }

        // 🔐 CREATE USER
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(password));

        if (req.getAdminKey() != null && adminSecret.equals(req.getAdminKey())) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.MEMBER);
        }

        userRepository.save(user);

        return ResponseEntity.ok("Signup successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        User existing = userRepository.findByEmail(user.getEmail())
                .orElseThrow();

        if (!passwordEncoder.matches(user.getPassword(), existing.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(existing.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", existing.getRole()
        ));
    }

}