package com.example.application.data;

import com.example.application.data.Tracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TrackerRepository extends JpaRepository<Tracker, Long> {

	@Query("select t from Tracker t " +
			"where lower(t.name) like lower(concat('%', :searchTerm, '%')) ")
	List<Tracker> search(@Param("searchTerm") String searchTerm);
}
