# `AppUserDetails`
Makes AppUser understandable to Spring Security

### Role
Security adapter (domain → Spring Security)

### What it is
A wrapper that adapts your AppUser entity to Spring Security’s UserDetails interface.
``` java
public class AppUserDetails implements UserDetails {
    private final AppUser user;
}
```
### What it does
* Translates AppUser → username, password, roles
* Provides data Spring Security needs to authenticate

### What it does NOT do
* ❌ Business logic
* ❌ Database access
* ❌ User lookup
* ❌ Authorization decisions

### Why it exists
* Spring Security does not know what AppUser is.
* It only understands UserDetails.
* This class is the bridge, nothing more.

### ❌ Never expose UserDetails outside security

# `AppUserDetailsService`
Load user credentials for Spring Security during login

### Role
Authentication provider (DB → Security)

### What it is
A mandatory Spring Security service used only during login.
``` java
@Service
public class AppUserDetailsService implements UserDetailsService
```
### What it does
* Loads a user by username
* Fetches AppUser from database
* Wraps it into AppUserDetails
``` java
loadUserByUsername(username)
```

### When it runs
* During login
* Before a user is authenticated
* When Spring Security needs credentials

* ❌ No user is authenticated yet
* ❌ No SecurityContext
* ❌ AuthenticationContext is empty
* ❌ SecurityService cannot work

### What it does NOT do
* ❌ Access the current user
* ❌ Use SecurityService
* ❌ Business logic
* ❌ UI interaction

### Why it owns AppUserRepository
Authentication must come from persistent storage
and no authenticated user exists yet.


# `SecurityConfig`
Wires security together

### Role
Security wiring & rules

### What it is
Spring Security configuration, not logic.
``` java
@Configuration
@EnableWebSecurity
public class SecurityConfig
```
### What it does
* Registers security filters
* Defines login view
* Declares PasswordEncoder
* Hooks Spring Security into Vaadin
* SecurityFilterChain
* PasswordEncoder

### What it does NOT do
* ❌ Load users
* ❌ Know about AppUser
* ❌ Handle sessions
* ❌ Business logic

### Why it exists
* Without it:
    * no authentication
    * no authorization
    * no password hashing
    * no login page
* This is infrastructure, not application logic.


# `SecurityService`
Tells the app who is currently logged-in

### Role
Current user access (Security → Domain)

### What it is
A helper facade for the rest of your app.
``` java
@Component
public class SecurityService
```
### What it does
* Reads the current authenticated principal
* Converts it into your domain user
* Exposes safe methods like:
* getCurrentUser()
* getCurrentUsername()
* logout()

### What it does NOT do
* ❌ Authenticate users
* ❌ Verify passwords
* ❌ Decide roles
* ❌ Replace Spring Security
* ❌ Return UserDetails

### Why it exists
So:
* UI doesn’t touch Spring Security internals
* Business code doesn’t depend on UserDetails
* There’s exactly one place to access “current user”


# The lifecycle
| Step | Class involved        | Purpose                 |
|------|-----------------------|-------------------------|
| 	1   | SecurityConfig        | Enables security        |
| 	2   | AppUserDetailsService | Loads user from DB      |
| 	3   | AppUserDetails        | Adapts user to security |
| 	4   | Spring Security       | Authenticates           |
| 	5   | SecurityService       | Access current user     |
| 	6   | Services / UI         | Use domain user         |


# Dependency direction
```
AppUser
↑
AppUserDetails
↑
AppUserDetailsService
↑
Spring Security
↓
SecurityService
↓
Business Services
↓
UI
```