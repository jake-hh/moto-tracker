package com.example.application.services;

import com.example.application.data.Tracker;
import com.example.application.data.Operation;
import com.example.application.data.Event;
import com.example.application.data.TrackerRepository;
import com.example.application.data.OperationRepository;
import com.example.application.data.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

	// public List<Operation> findAllOperations(String stringFilter) {
	// 	if (stringFilter == null || stringFilter.isEmpty()) {
	// 		return operationRepository.findAll();
	// 	} else {
	// 		return operationRepository.search(stringFilter);
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

	public List<Tracker> findAllTrackers() {
		return trackerRepository.findAll();
	}

	public List<Event> findAllEvents(){
		return eventRepository.findAll();
	}
}
