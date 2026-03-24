# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## See readme for basic information
@README.md

## Architecture

### Tech stack
- **Spring Boot 3.5.6 / Java 21** backend
- **Vaadin 24** server-side UI framework — all UI is Java, no separate frontend project
- **H2** in-memory database (data is lost on restart)
- **Spring Security** with form-based login

### Package structure
```
com.example.application
├── data/
│   ├── entity/          # JPA entities: AppUser, Vehicle, Tracker, Event, Operation, AppUserSettings
│   ├── repo/            # Spring Data JPA repositories
│   ├── BasicInterval    # Embeddable record: (amount, Unit) for maintenance schedules
│   ├── DashboardEventFormat  # Enum: LAST_SERVICE | NEXT_SERVICE | NEXT_SERVICE_RELATIVE
│   └── VehicleType      # Enum: Lorry, Car, Motorcycle, Moped, Other
├── security/            # Spring Security wiring (see doc/security.md for detailed roles)
├── services/
│   ├── MainService      # All CRUD for Vehicle, Tracker, Event, Operation — always @Transactional
│   ├── UserSettingsService  # Manages per-user settings (selected vehicle, dashboard format)
│   └── RegistrationService
└── ui/
    ├── views/           # Vaadin views (pages), each annotated @Route
    ├── events/          # Custom ComponentEvents for inter-view communication
    ├── components/      # Reusable Vaadin components
    ├── render/          # Grid cell renderers and comparators
    └── format/          # Human-readable formatters (distance, time)
```

### Domain model
```
AppUser → (owns) → Vehicle → (has) → Tracker
                           → (has) → Event → (has many) → Operation
                                                           ↕
                                                        Tracker
```
`Operation` is the join between an `Event` and a `Tracker` — it records that a tracker item was serviced in a given event.

### Event-driven UI communication
Views don't call each other directly. `MainLayout` is the event bus: views fire events on it and subscribe to events from it. The four events are:
- `VehicleSelectedEvent` — user changes the vehicle dropdown
- `TrackerChangedEvent` — a tracker was created/edited/deleted
- `EventChangedEvent` — a service event was created/edited/deleted
- `OperationChangedEvent` — an operation was created/deleted (carries `createdInEventItem` flag to avoid redundant refreshes)

All views that need to react to data changes subscribe in their constructor via `mainLayout.addXxxListener(e -> updateXxx())`.

### Vaadin scoping
Views and `MainLayout` are `@SpringComponent @UIScope` — one instance per browser tab. This is important: never use `@Singleton` for UI components.

### Security layer
The security architecture is documented in `doc/security.md`. The key rule: business code and UI only ever touch `SecurityService.getCurrentUser()` — never `UserDetails` or Spring Security internals directly.

### `MainService` conventions
- All public methods are `@Transactional` (class-level annotation).
- Methods that operate on the selected vehicle call `getSelectedVehicle()` internally — callers don't pass the vehicle.
- CRUD methods call `Notify.ok()`/`Notify.error()` as a side effect and re-throw as `RuntimeException` on failure.
- `isEmpty()` static helpers on entities guard against saving/deleting unsaved shells.
