package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;


public class EventItemController {

	private MainService service;
	private Event event;
	private List<Tracker> trackers;
	//private List<Operation> operations;

	public record OperationRender(List<Operation> operations, Integer emptyPos) {}

	public EventItemController(MainService service, Event event, List<Tracker> trackers) {
		this.service = service;
		this.event = event;
		this.trackers = trackers;
		//reload(event);
	}

	//public void reload(Event event) {
		//this.event = service.findUpdatedEvent(event);
		//this.operations = service.findAllOperations(this.event);
	//}

	public OperationRender getOperations(Integer newOperationPos) {
		List<Operation> operations = getOperations();
		@Nullable
		Integer emptyPos = null;

		if (operations.isEmpty()) {
			operations.add(newEmptyOperation());
			emptyPos = 0;
		}

		else if (newOperationPos != null) {
			operations.add(newOperationPos, newEmptyOperation());
			emptyPos = newOperationPos;
		}

		return new OperationRender(operations, emptyPos);
	}

	private Operation newEmptyOperation() {
		Operation op = new Operation();
		op.setEvent(event);
		return op;
	}

	public Event getUpdatedEvent() {
		return service.findEventById(event.getId()).get();
	}

	public List<Operation> getOperations() {
		return service.findAllOperations(event);
	}

	public void deleteEvent() {
		service.deleteEventById(event.getId());
	}

	public void deleteOperations(List<Operation> operations) {
		for (Operation operation : operations)
			service.deleteOperation(operation, false);
	}

	public void setMileage(Consumer<Integer> mileageFieldSetter) {
		if (event.getMileage() != null)
			mileageFieldSetter.accept(event.getMileage());
	}

	public void setDate(Consumer<LocalDate> dateFieldSetter) {
		if (event.getDate() != null)
			dateFieldSetter.accept(event.getDate());
	}

	public void updateEvent(Consumer<Event> mutator) {
		Event fresh = getUpdatedEvent();
		mutator.accept(fresh);
		service.saveEvent(fresh);
		event = fresh;
	}

	// --- Temporary during migration ---

	public MainService getService() {
		return service;
	}

	public List<Tracker> getTrackers() {
		return trackers;
	}
}
