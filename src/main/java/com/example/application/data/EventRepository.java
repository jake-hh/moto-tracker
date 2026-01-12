package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByVehicle(Vehicle vehicle);

	Optional<Event> findFirstByVehicleAndDateIsNotNullOrderByDateAsc(Vehicle vehicle);

	boolean existsByVehicle(Vehicle vehicle);
}
