package com.example.application.data;


public enum DashboardEventFormat {
	LAST_SERVICE("Last service"),
	NEXT_SERVICE("Next service"),
	NEXT_SERVICE_RELATIVE("Next service (relative)");

	private final String label;

	DashboardEventFormat(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
