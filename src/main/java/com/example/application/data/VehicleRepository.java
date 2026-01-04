package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	List<Vehicle> findByOwner(AppUser owner);
}
