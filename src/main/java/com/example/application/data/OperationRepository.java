package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OperationRepository extends JpaRepository<Operation, Long> {

	// @Query("select c from Operation c " +
	// 		"where lower(c.firstName) like lower(concat('%', :searchTerm, '%')) " +
	// 		"or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))")
	// List<Operation> search(@Param("searchTerm") String searchTerm);

    /* --- Batch lookup for all trackers --- */
    @Query("""
        SELECT o.tracker.id, MAX(e.date), MAX(e.mileage)
        FROM Operation o
        JOIN o.event e
        WHERE o.tracker IN :trackers
        GROUP BY o.tracker.id
    """)
    List<Object[]> findLatestMileagesByTrackers(@Param("trackers") List<Tracker> trackers);
}
