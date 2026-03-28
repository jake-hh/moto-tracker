package com.example.application.data.repo;

import com.example.application.data.entity.DefaultTracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface DefaultTrackerRepository extends JpaRepository<DefaultTracker, Long> {

	@Query("select dt from DefaultTracker dt " +
			"where lower(dt.name) like lower(concat('%', :searchTerm, '%'))")
	List<DefaultTracker> search(@Param("searchTerm") String searchTerm);
}
