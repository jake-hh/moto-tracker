package com.example.application.ui.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;


public class Footer extends HorizontalLayout {
	private final Span span = new Span();
	private String text;


	public Footer() {
		setHeight("var(--lumo-space-xl)");
		setSpacing(false);
		setPadding(false);

		span.addClassNames("mt-helper-text", "warning");
		add(span);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void showText(boolean enable) {
		span.setText(enable ? text : null);
	}
}
