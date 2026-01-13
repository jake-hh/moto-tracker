package com.example.application.data;


public enum DashboardMode {

	LAST_SERVICE("Last service"),
	NEXT_SERVICE("Next service"),
	NEXT_SERVICE_RELATIVE("Next service (relative)");

	private final String name;

	DashboardMode(String displayName) {
		this.name = displayName;
	}

	public String getName() {
		return name;
	}
}
