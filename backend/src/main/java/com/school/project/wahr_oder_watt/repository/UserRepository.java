package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for User entity, providing CRUD operations and custom queries.
 */
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}
