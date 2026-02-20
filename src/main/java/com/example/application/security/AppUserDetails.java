package com.example.application.security;

import com.example.application.data.entity.AppUser;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


/* Security adapter (domain → Spring Security) */

public class AppUserDetails implements UserDetails {

	private final AppUser user;


	public AppUserDetails(AppUser user) {
		this.user = user;
	}

	public AppUser getUser() {
		return user;
	}

	@Override public String getUsername() { return user.getUsername(); }
	@Override public String getPassword() { return user.getPasswordHash(); }

	@Override public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(); // no roles yet
	}

	@Override public boolean isAccountNonExpired()     { return true; }
	@Override public boolean isAccountNonLocked()      { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled()               { return true; }
}
