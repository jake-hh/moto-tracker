package com.example.application.data;


public enum DashboardEventFormat {
	LAST_SERVICE("Last service"),
	NEXT_SERVICE("Next service"),
	NEXT_SERVICE_RELATIVE("Next service (relative)");

	private final String name;

	DashboardEventFormat(String displayName) {
		this.name = displayName;
	}

	public String getName() {
		return name;
	}
}
