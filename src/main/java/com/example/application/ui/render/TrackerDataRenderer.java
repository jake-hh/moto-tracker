package com.example.application.ui.render;

import com.example.application.data.entity.Tracker;
import com.example.application.services.model.TrackerData;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Optional;
import java.util.function.Function;


public class TrackerDataRenderer {

	public static <T> ComponentRenderer<HorizontalLayout, Tracker> render(Function<Tracker, TrackerData.Status> statusLoader, Function<Tracker, Optional<T>> dataLoader) {
		return render(statusLoader, dataLoader, String::valueOf);
	}

	public static <T> ComponentRenderer<HorizontalLayout, Tracker> render(Function<Tracker, TrackerData.Status> statusLoader, Function<Tracker, Optional<T>> dataLoader, Function<T, String> dataFormatter) {
		return new ComponentRenderer<>(tracker -> {

			Optional<String> str = dataLoader.apply(tracker).map(dataFormatter);
			TrackerData.Status status = statusLoader.apply(tracker);

			String color;
			Icon icon;
			Span text = new Span(str.orElse(""));

			switch (status) {
				case OK -> {
					icon = VaadinIcon.CHECK.create();
					color = "var(--lumo-success-text-color)";
				}
				case APPROACHING -> {
					icon = VaadinIcon.WRENCH.create();
					color = "var(--lumo-warning-text-color)";
				}
				case OVERDUE -> {
					icon = VaadinIcon.EXCLAMATION_CIRCLE.create();
					color = "var(--lumo-error-text-color)";
				}
				default -> {
					icon = VaadinIcon.INFO_CIRCLE.create();
					//icon = null;
					color = "var(--lumo-secondary-text-color)";
				}
			}

			text.getStyle().set("color", color);
			icon.getStyle()
					.set("margin-right", "var(--lumo-space-s)")
					.set("color", color); // icon follows text color

			HorizontalLayout layout;
			if (str.isPresent())
				layout = new HorizontalLayout(icon, text);
			else
				layout = new HorizontalLayout();

			layout.setPadding(false);
			layout.setSpacing(false);
			layout.setAlignItems(FlexComponent.Alignment.CENTER);

			return layout;
		});
	}
}
