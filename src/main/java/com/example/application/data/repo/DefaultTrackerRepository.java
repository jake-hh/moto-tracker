package com.example.application.data.repo;

import com.example.application.data.entity.DefaultTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultTrackerRepository extends JpaRepository<DefaultTracker, Long> {}
