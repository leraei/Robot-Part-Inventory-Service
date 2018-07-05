package com.kahoot.robotpartservice.repository;

import com.kahoot.robotpartservice.model.RobotPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RobotPartRepository extends JpaRepository<RobotPart, Long> {
    Optional<RobotPart> findBySerialNumber(Long id);
}