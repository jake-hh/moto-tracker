package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@SuppressWarnings("FieldMayBeFinal")
public class EventItemController {

	private MainService service;
	private Event event;

	public record OperationRender(
			List<Operation> operations,
			@Nullable Integer emptyPos
	) {
		// Disable removeButton if it's the only item in list and is empty
		boolean cannotRemove(int pos) {
			return operations.size() == 1 && isEmpty(pos);
		}

		boolean isEmpty(int pos) {
			return emptyPos != null && pos == emptyPos;
		}
	}

	public EventItemController(MainService service, Event event) {
		this.service = service;
		this.event = event;
	}

	public OperationRender prepareOperations(Integer newOperationPos) {
		List<Operation> operations = getOperations();
		@Nullable Integer emptyPos = null;

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
		var op = new Operation();
		op.setEvent(event);
		return op;
	}

	public Event getUpdatedEvent() {
		return service.findUpdatedEvent(event);
	}

	public List<Operation> getOperations() {
		return service.findAllOperations(event);
	}

	public void deleteEvent() {
		service.deleteEventById(event.getId());
	}

	public void deleteOperation(Operation op) {
		// Select operation with id (operation has outdated version after saving in db trackerBox change event)
		service.deleteOperationById(op.getId());
	}

	public void deleteEventWithOperations() {
		service.deleteEventCascade(event.getId());
	}

	public int getOperationCount() {
		return service.findOperationCountByEventId(event.getId());
	}

	public void initMileageField(Consumer<Integer> mileageFieldSetter) {
		Optional.ofNullable(event.getMileage())
				.ifPresent(mileageFieldSetter);
	}

	public void initDateField(Consumer<LocalDate> dateFieldSetter) {
		Optional.ofNullable(event.getDate())
				.ifPresent(dateFieldSetter);
	}

	public void updateEvent(Consumer<Event> mutator, Runnable onFinished) {
		Event fresh = getUpdatedEvent();
		mutator.accept(fresh);
		service.saveEvent(fresh);
		event = fresh;
		onFinished.run();
	}

	public void updateOperation(Operation op, Tracker tracker) {
		// Get operation with updated version (operation has outdated version after saving in db trackerBox change event)
		Operation fresh = service.findUpdatedOperation(op);
		fresh.setTracker(tracker);
		service.saveOperation(fresh);
	}
}
