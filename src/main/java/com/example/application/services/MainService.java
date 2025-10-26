package com.example.application.services;

import com.example.application.data.Tracker;
import com.example.application.data.Operation;
import com.example.application.data.Event;
import com.example.application.data.TrackerRepository;
import com.example.application.data.OperationRepository;
import com.example.application.data.EventRepository;
import com.example.application.data.Pair;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public class MainService {

	private final OperationRepository operationRepository;
	private final TrackerRepository trackerRepository;
	private final EventRepository eventRepository;

	public MainService(OperationRepository operationRepository,
					  TrackerRepository trackerRepository,
					  EventRepository eventRepository) {
		this.operationRepository = operationRepository;
		this.trackerRepository = trackerRepository;
		this.eventRepository = eventRepository;
	}

	public List<Operation> findAllOperations() {
		return operationRepository.findAll();
	}

	// public List<Operation> findAllOperations(String filter) {
	// 	if (filter == null || filter.isEmpty()) {
	// 		return operationRepository.findAll();
	// 	} else {
	// 		return operationRepository.search(filter);
	// 	}
	// }

	// public long countOperations() {
	// 	return operationRepository.count();
	// }

	public void deleteOperation(Operation operation) {
		operationRepository.delete(operation);
	}

	public void saveOperation(Operation operation) {
		if (operation == null) {
			System.err.println("Operation is null. Are you sure you have connected your form to the application?");
			return;
		}
		operationRepository.save(operation);
	}

	public List<Tracker> findAllTrackers(String filter) {
		if (filter == null || filter.isEmpty()) {
			return findAllTrackers();
		} else {
			return trackerRepository.search(filter);
		}
	}

    public Map<Long, Pair<String, Integer>> findLastEventDataForTrackers(List<Tracker> trackers) {
        if (trackers.isEmpty()) {
			return Collections.emptyMap();
		}

        List<Object[]> results = operationRepository.findLatestMileagesByTrackers(trackers);

		// System.out.println("Results: " + results);
        // Map<Long, Pair<String, Integer>> map = results
		return results
			.stream()
			.collect(
				Collectors.toMap(
					row -> (Long) row[0],
					row -> new Pair<>((String) row[1], (Integer) row[2])  // [0]=tracker.id, [1]=date, [2]=mileage
		));
		// System.out.println("Map: " + map);
		// return map;
    }

	public void deleteTracker(Tracker tracker) {
		trackerRepository.delete(tracker);
	}

	public void saveTracker(Tracker tracker) {
		if (tracker == null) {
			System.err.println("Tracker is null. Are you sure you have connected your form to the application?");
			return;
		}
		trackerRepository.save(tracker);
	}

	public List<Tracker> findAllTrackers() {
		return trackerRepository.findAll();
	}

	public List<Event> findAllEvents(){
		return eventRepository.findAll();
	}
}
