package com.school.project.wahr_oder_watt.security;

import com.school.project.wahr_oder_watt.model.User;
import com.school.project.wahr_oder_watt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService implements UserDetailsService to load user-specific data.
 * It retrieves user information from the UserRepository and constructs a UserDetails object.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  /**
   * Loads the user by username or email.
   *
   * @param identifier the username or email identifying the user whose data is required.
   * @return a fully populated UserDetails object (never null)
   * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
   */
  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    // Prüfe, ob identifier eine E-Mail ist (z.B. enthält '@')
    if (identifier.contains("@")) {
      return loadUserByEmail(identifier);
    } else {
      // Standard: Suche nach Username
      User user = userRepository.findByUsername(identifier)
          .orElseThrow(
              () -> new UsernameNotFoundException("User not found with username: " + identifier));
      return org.springframework.security.core.userdetails.User
          .withUsername(user.getUsername())
          .password(user.getPassword())
          .authorities(user.isAdmin() ? "ADMIN" : "USER")
          .accountLocked(!user.isEnabled())
          .build();
    }
  }
  /**
   * Loads the user by email.
   *
   * @param email the email identifying the user whose data is required.
   * @return a fully populated UserDetails object (never null)
   * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
   */
  public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities(user.isAdmin() ? "ADMIN" : "USER") // You can set roles/authorities as needed
        .accountLocked(!user.isEnabled())
        .build();
  }

}
