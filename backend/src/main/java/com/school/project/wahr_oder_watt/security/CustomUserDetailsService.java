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
   * Loads the user by username.
   *
   * @param username the username identifying the user whose data is required.
   * @return a fully populated UserDetails object (never null)
   * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities(user.isAdmin() ? "ADMIN" : "USER") // You can set roles/authorities as needed
        .accountLocked(!user.isEnabled())
        .build();
  }

}
