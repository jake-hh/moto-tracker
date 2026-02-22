package com.example.application.ui.render;

import com.example.application.data.DashboardEventFormat;
import com.example.application.data.entity.Tracker;
import com.example.application.services.model.TrackerData;
import com.example.application.ui.format.HumanDistanceFormatter;
import com.example.application.ui.format.HumanTimeFormatter;
import com.example.application.util.Time;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.vaadin.lineawesome.LineAwesomeIcon;

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

	private static Component render(Status status, String text) {
		String color;
		Component icon;

		switch (status) {
			case OK -> {
				icon = LineAwesomeIcon.CHECK_SOLID.create();
				//icon = LineAwesomeIcon.CHECK_CIRCLE.create();
				color = "mt-success-color";
			}
			case APPROACHING -> {
				icon = LineAwesomeIcon.TOOLS_SOLID.create();
				color = "mt-warning-color";
			}
			case OVERDUE -> {
				//icon = LineAwesomeIcon.EXCLAMATION_CIRCLE_SOLID.create();
				icon = LineAwesomeIcon.EXCLAMATION_TRIANGLE_SOLID.create();
				color = "mt-error-color";
			}
			default -> {
				icon = LineAwesomeIcon.INFO_CIRCLE_SOLID.create();
				//icon = null;
				color = "mt-secondary-color";
			}
		}

		Span span = new Span(text);

		icon.addClassName("mt-status-icon");
		icon.addClassName(color);
		span.addClassName(color);

		var bar = new HorizontalLayout(icon, span);
		bar.setVisible(text != null);
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
