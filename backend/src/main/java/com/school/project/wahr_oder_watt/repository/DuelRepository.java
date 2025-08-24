package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.model.Duel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Duel entities.
 */
public interface DuelRepository extends JpaRepository<Duel, Long> {
}
