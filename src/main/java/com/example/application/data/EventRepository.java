package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Long> {

	boolean existsByVehicle(Vehicle vehicle);
}
