package com.example.application.data;

import jakarta.persistence.*;


@SuppressWarnings("unused")
@Entity
//@Table(name = "user_settings")
public class AppUserSettings extends AbstractEntity {

	@OneToOne(optional = false)
	private AppUser user;

	@ManyToOne
	private Vehicle selectedVehicle;

	@Enumerated(EnumType.STRING)
	private DashboardEventFormat dashboardEventFormat;


	public AppUserSettings() { }

	public AppUserSettings(AppUser user) {
		this.user = user;
	}

	public AppUser getUser() {
		return user;
	}

	public Vehicle getSelectedVehicle() {
		return selectedVehicle;
	}

	public DashboardEventFormat getDashboardEventFormat() {
		return dashboardEventFormat;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public void setSelectedVehicle(Vehicle selectedVehicle) {
		this.selectedVehicle = selectedVehicle;
	}

	public void setDashboardEventFormat(DashboardEventFormat format) {
		this.dashboardEventFormat = format;
	}
}
