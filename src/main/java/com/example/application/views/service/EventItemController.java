package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import jakarta.annotation.Nullable;

import java.util.List;


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
		List<Operation> operations = service.findAllOperations(event);
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


	// --- Temporary during migration ---

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public MainService getService() {
		return service;
	}

	public List<Tracker> getTrackers() {
		return trackers;
	}
}
