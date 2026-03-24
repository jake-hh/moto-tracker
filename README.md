# Moto Tracker

A vehicle maintenance and service tracking web app.
Users can manage vehicles, define maintenance trackers (e.g., "oil change every 6 months"), log service events, and link operations to those events — keeping a full history of what maintenance was done and when.

## Tech Stack

- **Backend**: Spring Boot 3.5.6, Java 21, Spring Data JPA + Hibernate, H2 (embedded DB), Spring Security
- **Frontend**: Vaadin 24 (server-driven Java UI framework), custom Lumo CSS theme
- **Build**: Maven
- **PWA-enabled** for offline/installable support

## Views

| View           | Purpose                                                |
|----------------|--------------------------------------------------------|
| Dashboard      | Overview of all trackers with latest/next service info |
| Vehicle        | Create/edit vehicles                                   |
| Tracker        | Create/edit maintenance trackers                       |
| Service        | Log service events and attach operations               |
| Operations     | (deprecated) Manage operations list                    |
| Profile        | User settings                                          |
| Login/Register | Authentication screens                                 |

## Core Domain

| Entity      | Description                                                                                                                                                   |
|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AppUser`   | Registered user account                                                                                                                                       |
| `Vehicle`   | Car/motorcycle/moped/etc. owned by a user (make, model, mileage, type, etc.)                                                                                  |
| `Tracker`   | A maintenance/service to be performed on a vehicle (e.g., oil change, tire change, engine repair), may include rules (e.g., every 15000 km or every 6 months) |
| `Event`     | A service session recorded for a vehicle (date + mileage)                                                                                                     |
| `Operation` | Links an `Event` to a `Tracker` — records that a tracker item was addressed in a service event                                                                |

## Commands

```bash
# Run in development mode (default goal)
mvn spring-boot:run

# Build (skips frontend compilation)
mvn package

# Build for production (compiles and bundles frontend)
mvn package -Pproduction

# Run unit tests
mvn test

# Run integration/E2E tests (starts server, runs *IT tests, stops server)
mvn verify -Pit

# Run a single test class
mvn test -Dtest=LoginE2ETest
```

The app runs on `http://localhost:8080` by default. The JVM debug port is **5895** (configured in `pom.xml`).
