package com.example.application.services;

import com.example.application.Notify;
import com.example.application.data.Tracker;
import com.example.application.data.Operation;
import com.example.application.data.Event;
import com.example.application.data.TrackerRepository;
import com.example.application.data.OperationRepository;
import com.example.application.data.EventRepository;
import com.example.application.data.Pair;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


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

	  ///////////////////////////////////////
	 // ---- OPERATION -- REPOSITORY ---- //
	///////////////////////////////////////

	public List<Operation> findAllOperations() {
		return operationRepository.findAll();
	}

	public List<Operation> findAllOperations(Event event) {
		//Notify.warn("fetching operations");
		if (event == null || event.getId() == null)
			return new ArrayList<>();
		else
			return operationRepository.findByEvent(event);
	}

	// Get operation with updated version
	public Optional<Operation> findOperationById(Long id) {
		if (id != null)
			return operationRepository.findById(id);
		else
			return Optional.empty();
	}

	public Operation findUpdatedOperation(Operation operation) {
		// Get operation with updated version
		return findOperationById(operation.getId()).orElse(operation);
	}

	public int findOperationCountByEventId(Long id) {
		return operationRepository.countByEvent_Id(id);
	}

	public void deleteOperation(Operation operation, boolean showOkNotif) {
		try {
			operationRepository.delete(operation);
			if (showOkNotif)
				Notify.ok("Deleted operation");
		} catch (Exception e) {
			Notify.warn("Failed to delete operation: " + e.getMessage());
			throw new RuntimeException("Could not delete operation", e);
		}
	}

	public void deleteOperationById(Long id) {
		// Get operation with updated version
		findOperationById(id).ifPresent(op -> deleteOperation(op, true));
	}

	public void saveOperation(Operation operation) {
		//Notify.info(operation.toString());

		if (operation == null) {
			Notify.warn("Operation is null. Are you sure you have connected your form to the application?");
		}
		else if (operation.getEvent() == null) {
			Notify.warn(operation + " must be linked to an Event before saving");
		}
		else if (operation.getTracker() != null) {
			try {
				operationRepository.save(operation);
				Notify.ok("Saved operation");
			} catch (Exception e) {
				Notify.warn("Failed to save operation: " + e.getMessage());
				throw new RuntimeException("Could not save operation", e);
			}
		}
	}

	  /////////////////////////////////////
	 // ---- TRACKER -- REPOSITORY ---- //
	/////////////////////////////////////

	public List<Tracker> findAllTrackers(String filter) {
		if (filter == null || filter.isEmpty()) {
			return findAllTrackers();
		} else {
			return trackerRepository.search(filter);
		}
	}

	public List<Tracker> findAllTrackers() {
		return trackerRepository.findAll();
	}

	public boolean isTrackerUsed(Tracker tracker) {
		return operationRepository.existsByTracker(tracker);
	}

	public Map<Long, Pair<LocalDate, Integer>> findLastEventDataForTrackers(List<Tracker> trackers) {
        if (trackers.isEmpty()) {
			return Collections.emptyMap();
		}

        List<Object[]> results = operationRepository.findLatestEventDatesAndMileagesForTrackers(trackers);

		return results
			.stream()
			.collect(
				Collectors.toMap(
					row -> (Long) row[0],
					row -> new Pair<>((LocalDate) row[1], (Integer) row[2])  // [0]=tracker.id, [1]=date, [2]=mileage
		));
    }

	public void deleteTracker(Tracker tracker) {
		try {
			trackerRepository.delete(tracker);
			Notify.ok("Deleted event");
		} catch (Exception e) {
			Notify.warn("Failed to delete tracker: " + e.getMessage());
			throw new RuntimeException("Could not delete tracker", e);
		}
	}

	public void saveTracker(Tracker tracker) {
		if (tracker == null) {
			Notify.warn("Tracker is null. Are you sure you have connected your form to the application?");
			return;
		}

		try {
			trackerRepository.save(tracker);
			Notify.ok("Saved tracker");
		} catch (Exception e) {
			Notify.warn("Failed to save tracker: " + e.getMessage());
			throw new RuntimeException("Could not save tracker", e);
		}
	}

	  ///////////////////////////////////
	 // ---- EVENT -- REPOSITORY ---- //
	///////////////////////////////////

	public List<Event> findAllEvents(){
		return eventRepository.findAll();
	}

	// Get operation with updated version
	private Optional<Event> findEventById(Long id) {
		if (id != null)
			return eventRepository.findById(id);
		else
			return Optional.empty();
	}

	public Event findUpdatedEvent(Event event) {
		// Get event with updated version
		return findEventById(event.getId()).orElse(event);
	}

	public void deleteEvent(Event event) {
		try {
			eventRepository.delete(event);
			Notify.ok("Deleted event");
		} catch (Exception e) {
			Notify.warn("Failed to delete event: " + e.getMessage());
			throw new RuntimeException("Could not delete event", e);
		}
	}

	public void deleteEventById(Long id) {
		// Get operation with updated version
		findEventById(id).ifPresent(this::deleteEvent);
	}

	@Transactional
	public void deleteEventCascade(Long eventId) {
		operationRepository.deleteByEvent_Id(eventId);
		eventRepository.deleteById(eventId);
	}

	public void saveEvent(Event event) {
		if (event == null) {
			Notify.warn("Event is null. Are you sure you have connected your form to the application?");
		}
		else if (event.getDate() == null) {
			Notify.warn(event + " needs a date before saving");
		}
		else {
			try {
				eventRepository.save(event);
				Notify.ok("Saved event");
			} catch (Exception e) {
				Notify.warn("Failed to save event: " + e.getMessage());
				throw new RuntimeException("Could not save event", e);
			}
		}
	}
}
