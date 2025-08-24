package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.model.Highscore;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Highscore entities.
 */
public interface HighscoreRepository extends JpaRepository<Highscore, Long> {
}
