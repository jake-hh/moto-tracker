package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TrackerRepository extends JpaRepository<Tracker, Long> {

	@Query("select t from Tracker t " +
			"where lower(t.name) like lower(concat('%', :searchTerm, '%')) ")
	List<Tracker> searchAll(@Param("searchTerm") String searchTerm);

	@Query("select t from Tracker t " +
			"where lower(t.name) like lower(concat('%', :searchTerm, '%')) " +
			"and t.vehicle = :vehicle")
	List<Tracker> searchByVehicleId(
			@Param("vehicle") Vehicle vehicle,
			@Param("searchTerm") String searchTerm
	);

	List<Tracker> findByVehicle(Vehicle vehicle);
}
