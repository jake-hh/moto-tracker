package com.example.application.ui.render;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.services.model.TrackerData;
import com.example.application.ui.format.HumanDistanceFormatter;
import com.example.application.ui.format.HumanTimeFormatter;
import com.example.application.util.Time;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.time.LocalDate;
import java.util.Optional;


public class TrackerDataRenderer {

	enum Status {
		OK,
		APPROACHING,
		OVERDUE,
		UNKNOWN
	}

	public static ComponentRenderer<Component, Tracker> renderDate(TrackerData data, DashboardEventFormat format) {
		return new ComponentRenderer<>(tracker -> {

			Optional<LocalDate> lastDate = data.getLastDate(tracker);
			Optional<LocalDate> nextDate = data.getNextDate(tracker);
			Optional<Long> remainingDays = nextDate.map(data::calculateRemainingDays);

			Status status = getDateStatus(remainingDays.orElse(null));

			return switch (format) {
				case LAST_SERVICE ->          render(status, lastDate.map(String::valueOf).orElse(null));
				case NEXT_SERVICE ->          render(status, nextDate.map(String::valueOf).orElse(null));
				case NEXT_SERVICE_RELATIVE -> render(status, remainingDays.map(HumanTimeFormatter::formatRounded).orElse(null));
			};
		});
	}

	public static ComponentRenderer<Component, Tracker> renderMileage(TrackerData data, DashboardEventFormat format) {
		return new ComponentRenderer<>(tracker -> {

			Optional<Integer> lastMileage = data.getLastMileage(tracker);
			Optional<Integer> nextMileage = data.getNextMileage(tracker);
			Optional<Integer> remainingDistance = nextMileage.map(data::calculateRemainingDistance);

			Status status = getMileageStatus(remainingDistance.orElse(null));

			return switch (format) {
				case LAST_SERVICE ->          render(status, lastMileage.map(String::valueOf).orElse(null));
				case NEXT_SERVICE ->          render(status, nextMileage.map(String::valueOf).orElse(null));
				case NEXT_SERVICE_RELATIVE -> render(status, remainingDistance.map(HumanDistanceFormatter::format).orElse(null));
			};
		});
	}

	private static Component render(Status status, String str) {
		String color;
		Icon icon;

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

		Span text = new Span();
		if (str != null)
			text.setText(str);

		text.getStyle().set("color", color);
		icon.getStyle()
				.set("margin-right", "var(--lumo-space-s)")
				.set("color", color); // icon follows text color

		var bar = new HorizontalLayout(icon, text);;
		bar.setVisible(str != null);
		bar.setPadding(false);
		bar.setSpacing(false);
		bar.setAlignItems(FlexComponent.Alignment.CENTER);

		return bar;
	}

	private static Status getDateStatus(Long days) {
		if (days == null)
			return Status.UNKNOWN;
		if (days < 0)
			return Status.OVERDUE;
		if (days < Time.DAYS_IN_MONTH * 3)
			return Status.APPROACHING;
		else
			return Status.OK;
	}

	private static Status getMileageStatus(Integer distance) {
		if (distance == null)
			return Status.UNKNOWN;
		if (distance < 0)
			return Status.OVERDUE;
		if (distance < 5000)
			return Status.APPROACHING;
		else
			return Status.OK;
	}
}
