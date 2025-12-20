package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OperationRepository extends JpaRepository<Operation, Long> {

    /* --- Batch lookup for all trackers --- */
    @Query("""
        SELECT o.tracker.id, MAX(e.date), MAX(e.mileage)
        FROM Operation o
        JOIN o.event e
        WHERE o.tracker IN :trackers
        GROUP BY o.tracker.id
    """)
    List<Object[]> findLatestEventDatesAndMileagesForTrackers(@Param("trackers") List<Tracker> trackers);

	List<Operation> findByEvent(Event event);

	boolean existsByTracker(Tracker tracker);
}
