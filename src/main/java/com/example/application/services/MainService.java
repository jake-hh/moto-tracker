package com.example.application.services;

import com.example.application.Notify;
import com.example.application.data.*;
import com.example.application.security.SecurityService;
import com.example.application.views.EventData;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class MainService {

	private final VehicleRepository vehicleRepository;
	private final OperationRepository operationRepository;
	private final TrackerRepository trackerRepository;
	private final EventRepository eventRepository;

	private final SecurityService securityService;
	private final ObjectProvider<UserSettingsService> settingsProvider;

	public MainService(
			VehicleRepository vehicleRepository,
			OperationRepository operationRepository,
			TrackerRepository trackerRepository,
			EventRepository eventRepository,

			SecurityService securityService,
			ObjectProvider<UserSettingsService> settingsProvider
	) {
		this.vehicleRepository = vehicleRepository;
		this.operationRepository = operationRepository;
		this.trackerRepository = trackerRepository;
		this.eventRepository = eventRepository;

		this.securityService = securityService;
		this.settingsProvider = settingsProvider;
	}

	public static LocalDate getDateToday() {
		return LocalDate.now(ZoneId.systemDefault());
	}

	private UserSettingsService getSettingsService() {
		return settingsProvider.getObject();
	}

	private Optional<Vehicle> getSelectedVehicle() {
		return getSettingsService().getSelectedVehicle();
	}

	  ////////////////////////
	 // ---- VEHICLES ---- //
	////////////////////////

	public List<Vehicle> findVehicles() {
		return vehicleRepository.findByOwner(securityService.getCurrentUser());
	}

	public boolean isVehicleUsed(@NotNull Vehicle vehicle) {
		return eventRepository.existsByVehicle(vehicle);
	}

	public Vehicle createVehicle() {
		return new Vehicle(securityService.getCurrentUser(), getDateToday());
	}

	public void deleteVehicle(@NotNull Vehicle vehicle) {
		try {
			getSettingsService().unselectDeletedVehicle(vehicle, this);
			vehicleRepository.delete(vehicle);
			Notify.ok("Deleted vehicle");
		} catch (Exception e) {
			Notify.error("Failed to delete vehicle: " + e.getMessage());
			throw new RuntimeException("Could not delete vehicle", e);
		}
	}

	public void saveVehicle(@NotNull Vehicle vehicle) {
		if (vehicle == null) {
			Notify.error("Vehicle is null. Are you sure you have connected your form to the application?");
			return;
		}

		try {
			vehicleRepository.save(vehicle);
			Notify.ok("Saved vehicle");
		} catch (Exception e) {
			Notify.error("Failed to save vehicle: " + e.getMessage());
			throw new RuntimeException("Could not save vehicle", e);
		}
		getSettingsService().setSelectedVehicleIfEmpty(vehicle);
	}

	  //////////////////////////
	 // ---- OPERATIONS ---- //
	//////////////////////////

	public List<Operation> findAllOperations() {
		return operationRepository.findAll();
	}

	public List<Operation> findOperations() {
		return getSelectedVehicle()
				.map(operationRepository::findByEvent_Vehicle)
				.orElse(List.of());
	}

	public List<Operation> findOperationsByEventId(Long eventId) {
		//Notify.error("fetching operations");
		if (eventId == null)
			return List.of();
		else
			return operationRepository.findByEvent_Id(eventId);
	}

	// Get operation with updated version
	public Optional<Operation> findOperationById(Long id) {
		if (id != null)
			return operationRepository.findById(id);
		else
			return Optional.empty();
	}

	public Operation findUpdatedOperation(@NotNull Operation operation) {
		// Get operation with updated version
		return findOperationById(operation.getId()).orElse(operation);
	}

	public int findOperationCountByEventId(@NotNull Long id) {
		return operationRepository.countByEvent_Id(id);
	}

	public Optional<Operation> createOperation() {
		if (getSelectedVehicle().isPresent())
			return Optional.of(new Operation());
		else {
			Notify.error("No vehicle has been selected");
			return Optional.empty();
		}
	}

	public void deleteOperation(@NotNull Operation operation) {
		try {
			operationRepository.delete(operation);
			Notify.ok("Deleted operation");
		} catch (Exception e) {
			Notify.error("Failed to delete operation: " + e.getMessage());
			throw new RuntimeException("Could not delete operation", e);
		}
	}

	public void deleteOperationById(@NotNull Long id) {
		try {
			operationRepository.deleteById(id);
			Notify.ok("Deleted operation");
		} catch (Exception e) {
			Notify.error("Failed to delete operation: " + e.getMessage());
			throw new RuntimeException("Could not delete operation", e);
		}
	}

	public void saveOperation(@NotNull Operation operation) {
		//Notify.info(operation.toString());

		if (operation == null) {
			Notify.error("Operation is null. Are you sure you have connected your form to the application?");
		}
		else if (operation.getEvent() == null) {
			Notify.error(operation + " must be linked to an Event before saving");
		}
		else if (operation.getTracker() != null) {
			try {
				operationRepository.save(operation);
				Notify.ok("Saved operation");
			} catch (Exception e) {
				Notify.error("Failed to save operation: " + e.getMessage());
				throw new RuntimeException("Could not save operation", e);
			}
		}
	}

	  ////////////////////////
	 // ---- TRACKERS ---- //
	////////////////////////

	public List<Tracker> findAllTrackers(String filter) {
		if (filter == null || filter.isEmpty()) {
			return findAllTrackers();
		} else {
			return trackerRepository.searchAll(filter);
		}
	}

	public List<Tracker> findAllTrackers() {
		return trackerRepository.findAll();
	}

	public List<Tracker> findTrackers(String filter) {
		if (filter == null || filter.isEmpty())
			return findTrackers();
		else
			return getSelectedVehicle()
					.map(v -> trackerRepository.searchByVehicle(v, filter))
					.orElse(List.of());
	}

	public List<Tracker> findTrackers() {
		return getSelectedVehicle()
				.map(trackerRepository::findByVehicle)
				.orElse(List.of());
	}

	public boolean isTrackerUsed(@NotNull Tracker tracker) {
		return operationRepository.existsByTracker(tracker);
	}

	public EventData findLastEventDataForTrackers(@NotNull List<Tracker> trackers) {
		Map<Long, Pair<LocalDate, Integer>> dataMap;

        if (trackers.isEmpty())
			dataMap = Collections.emptyMap();
		else
			dataMap = operationRepository.findLatestEventDatesAndMileagesForTrackers(trackers)
					.stream()
					.collect(
						Collectors.toMap(
								// [0]=tracker.id, [1]=date, [2]=mileage
								row -> (Long) row[0],
								row -> new Pair<>((LocalDate) row[1], (Integer) row[2])
						));

		return new EventData(dataMap);
    }

	public Optional<Tracker> createTracker() {
		var tracker = getSelectedVehicle().map(Tracker::new);

		if (tracker.isEmpty())
			Notify.error("No vehicle has been selected");

		return tracker;
	}

	public void deleteTracker(@NotNull Tracker tracker) {
		try {
			trackerRepository.delete(tracker);
			Notify.ok("Deleted tracker");
		} catch (Exception e) {
			Notify.error("Failed to delete tracker: " + e.getMessage());
			throw new RuntimeException("Could not delete tracker", e);
		}
	}

	public void saveTracker(@NotNull Tracker tracker) {
		if (tracker == null) {
			Notify.error("Tracker is null. Are you sure you have connected your form to the application?");
			return;
		}

		try {
			trackerRepository.save(tracker);
			Notify.ok("Saved tracker");
		} catch (Exception e) {
			Notify.error("Failed to save tracker: " + e.getMessage());
			throw new RuntimeException("Could not save tracker", e);
		}
	}

	  //////////////////////
	 // ---- EVENTS ---- //
	//////////////////////

	public List<Event> findAllEvents(){
		return eventRepository.findAll();
	}

	public List<Event> findEvents() {
		return getSelectedVehicle()
				.map(eventRepository::findByVehicle)
				.orElse(List.of());
	}

	// Get event with updated version
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

	/*public void deleteEvent(Event event) {
		try {
			eventRepository.delete(event);
			Notify.ok("Deleted event");
		} catch (Exception e) {
			Notify.error("Failed to delete event: " + e.getMessage());
			throw new RuntimeException("Could not delete event", e);
		}
	}*/

	public void deleteEventById(@NotNull Long id) {
		try {
			eventRepository.deleteById(id);
			Notify.ok("Deleted event");
		} catch (Exception e) {
			Notify.error("Failed to delete event: " + e.getMessage());
			throw new RuntimeException("Could not delete event", e);
		}
	}

	@Transactional
	public void deleteEventCascade(@NotNull Long eventId) {
		try {
			operationRepository.deleteByEvent_Id(eventId);
			eventRepository.deleteById(eventId);
			Notify.ok("Deleted event with operations");
		} catch (Exception e) {
			Notify.error("Failed to delete event with operations: " + e.getMessage());
			throw new RuntimeException("Could not delete event with operations", e);
		}
	}

	public boolean createAndSaveEvent() {
		Optional<Vehicle> vehicle = getSelectedVehicle();

		vehicle.ifPresentOrElse(
				v -> saveEvent(new Event(v, getDateToday())),
				() -> Notify.error("No vehicle has been selected"));

		return vehicle.isPresent();
	}

	public void saveEvent(@NotNull Event event) {
		if (event == null) {
			Notify.error("Event is null. Are you sure you have connected your form to the application?");
		}
		else if (event.getDate() == null) {
			Notify.error(event + " needs a date before saving");
		}
		else {
			try {
				eventRepository.save(event);
				Notify.ok("Saved event");
			} catch (Exception e) {
				Notify.error("Failed to save event: " + e.getMessage());
				throw new RuntimeException("Could not save event", e);
			}
		}
	}
}
