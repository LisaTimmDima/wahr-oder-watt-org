package com.school.project.wahr_oder_watt.controller;

import com.school.project.wahr_oder_watt.dto.RegisterRequest;
import com.school.project.wahr_oder_watt.dto.LoginRequest;
import com.school.project.wahr_oder_watt.dto.AdminLoginRequest;
import com.school.project.wahr_oder_watt.security.JwtUtil;
import com.school.project.wahr_oder_watt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.school.project.wahr_oder_watt.model.User;

/**
 * AuthController handles authentication requests such as user login. It uses AuthenticationManager
 * to authenticate users and JwtUtil to generate JWT tokens.
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserService userService;

  /**
   * Authenticates the user and generates a JWT token upon successful authentication.
   *
   * @param request the login request containing username and password.
   * @return a ResponseEntity containing the JWT token if authentication is successful.
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = jwtUtil.generateToken(userDetails.getUsername());
    return ResponseEntity.ok(token);
  }
  @PostMapping("/login_admin")
  public ResponseEntity<?> loginAdmin(@RequestBody AdminLoginRequest request) {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
      );
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      // User anhand Username/E-Mail holen
      User user = userService.findByUsername(userDetails.getUsername());
      if (user == null || !user.isAdmin()) {
          return ResponseEntity.status(403).body("Kein Admin-Zugang!");
      }
      String token = jwtUtil.generateToken(userDetails.getUsername());
      return ResponseEntity.ok(token);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    userService.registerNewUser(request);
    String token = jwtUtil.generateToken(request.getUsername());
    return ResponseEntity.ok(token);
  }
}
