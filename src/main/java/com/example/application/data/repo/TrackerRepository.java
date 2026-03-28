package com.example.application.data.repo;

import com.example.application.data.entity.Tracker;
import com.example.application.data.entity.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TrackerRepository extends JpaRepository<Tracker, Long> {

	List<Tracker> findByNameContainingIgnoreCase(String filter);

	List<Tracker> findByVehicleAndNameContainingIgnoreCase(Vehicle vehicle, String filter);

	List<Tracker> findByVehicle(Vehicle vehicle);

	int countByVehicle(Vehicle vehicle);

	void deleteByVehicle(Vehicle vehicle);
}
