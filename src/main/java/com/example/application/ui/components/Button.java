package com.example.application.ui.components;


public class Button extends com.vaadin.flow.component.button.Button {

	//deleteBtn.hasClassName("mt-inactive-btn")
	private boolean active = true;
	private String inactiveTooltipText = null;


	public Button(String label) {
		super(label);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		setClassName("mt-inactive-btn", !active);
		setTooltipIfActive();
	}

	/* setting this text to null doesn't reset the tooltip text */
	public void setInactiveTooltipText(String text) {
		inactiveTooltipText = text;
		setTooltipIfActive();
	}

	private void setTooltipIfActive() {
		if (inactiveTooltipText != null)
			setTooltipText(active ? null : inactiveTooltipText);
	}
}
