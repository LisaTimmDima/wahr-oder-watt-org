package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.model.DuelRound;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing DuelRound entities.
 */
public interface DuelRoundRepository extends JpaRepository<DuelRound, Long> {
}
