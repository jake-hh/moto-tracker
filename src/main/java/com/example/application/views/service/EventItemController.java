package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class EventItemController {

	private final MainService service;
	private Event event;

	public EventItemController(MainService service, Event event) {
		this.service = service;
		this.event = event;
	}

	// --- OPERATION LIST ---

	public List<Operation> getOperations() {
		return service.findOperationsByEventId(event.getId());
	}

	public void deleteEvent() {
		service.deleteEventById(event.getId());
	}

	public void deleteOperation(Operation op) {
		// Select operation with id (operation has outdated version after saving in db trackerBox change event)
		if (op != null)
			service.deleteOperationById(op.getId());
	}

	public void deleteEventWithOperations() {
		service.deleteEventCascade(event.getId());
	}

	public int getOperationCount() {
		return service.findOperationCountByEventId(event.getId());
	}

	// --- EVENT ---

	public Optional<Integer> getMileage() {
		return Optional.ofNullable(event.getMileage());
	}

	public Optional<LocalDate> getDate() {
		return Optional.ofNullable(event.getDate());
	}

	@Transactional
	public void updateMileage(Integer mileage) {
		Event event = service.findUpdatedEvent(this.event);
		event.setMileage(mileage);

		service.bumpVehicleMileage(event);
		service.saveEvent(event);
		this.event = event;
	}

	public void updateDate(LocalDate date) {
		Event event = service.findUpdatedEvent(this.event);
		event.setDate(date);
		service.saveEvent(event);
		this.event = event;
	}

	// --- OPERATION ---

	public void updateOperation(Operation op, Tracker tracker) {
		// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
		Operation fresh = service.findUpdatedOperation(op);
		fresh.setTracker(tracker);
		service.saveOperation(fresh);
	}

	public void createOperation(Tracker tracker) {
		service.saveOperation(new Operation(event, tracker));
	}
}
