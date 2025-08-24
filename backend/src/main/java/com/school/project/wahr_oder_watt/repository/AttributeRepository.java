package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Attribute entities.
 */
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}
