package com.example.application.views.service;

import com.example.application.data.Event;
import com.example.application.data.Operation;
import com.example.application.data.Tracker;
import com.example.application.services.MainService;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class EventItemController {

	private final MainService service;
	private Event event;

	public record OperationRow(
			@Nullable Operation operation,
			int nextPos,
			boolean canAdd,
			boolean canRemove,
			boolean hasLabel
	) {
		static OperationRow from(Operation op, int nextPos, boolean canAdd, boolean hasLabel) {
			return new OperationRow(op, nextPos, canAdd, true, hasLabel);
		}

		static OperationRow empty(int nextPos, boolean canRemove, boolean hasLabel) {
			return new OperationRow(null, nextPos, false, canRemove, hasLabel);
		}
	}

	public EventItemController(MainService service, Event event) {
		this.service = service;
		this.event = event;
	}

	public List<OperationRow> getOperationRows(@Nullable Integer newOperationPos) {
		List<Operation> ops = service.findAllOperations(event);
		List<OperationRow> rows = new ArrayList<>();

		var size = ops.size();

		if (size == 0) {
			rows.add(OperationRow.empty(1, false, true));
			return rows;
		}

		else for (int i = 0; i < size; i++) {
			if (newOperationPos != null && newOperationPos == i) {
				rows.add(OperationRow.empty(i, true, i == 0));
			}

			boolean cannotAdd = newOperationPos != null && newOperationPos == i + 1;

			rows.add(OperationRow.from(ops.get(i), i + 1, !cannotAdd, i == 0));
		}

		if (newOperationPos != null && newOperationPos == size) {
			rows.add(OperationRow.empty(size, true, false));
		}

		return rows;
	}

	public Event getUpdatedEvent() {
		return service.findUpdatedEvent(event);
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
		Operation fresh = op == null ? new Operation(event) : service.findUpdatedOperation(op);
		fresh.setTracker(tracker);
		service.saveOperation(fresh);
	}
}
