package com.example.application.views.dashboard;

import com.example.application.data.*;
import com.example.application.services.MainService;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public record EventData(Map<Long, Pair<LocalDate, Integer>> dataMap, Vehicle vehicle, Optional<Event> firstEvent) {

	public enum Status {
		OK,
		APPROACHING,
		OVERDUE,
		UNKNOWN
	}

	private Optional<Pair<LocalDate, Integer>> getPair(Tracker tracker) {
		return Optional.ofNullable(dataMap.get(tracker.getId()));
	}

	public Optional<LocalDate> getLastDate(Tracker tracker) {
		return getPair(tracker).map(Pair::first);
	}

	public Optional<Integer> getLastMileage(Tracker tracker) {
		return getPair(tracker).map(Pair::second);
	}

	public Optional<LocalDate> getNextDate(Tracker tracker) {
		return getLastDate(tracker)
				.or(() -> firstEvent.map(Event::getDate))
				.flatMap(date ->
						Optional.ofNullable(tracker.getInterval())
								.map(BasicInterval::toPeriod)
								.map(date::plus));
	}

	public Optional<Integer> getNextMileage(Tracker tracker) {
		return getLastMileage(tracker)
				.or(() -> firstEvent.map(Event::getMileage))
				.flatMap(mileage ->
						Optional.ofNullable(tracker.getRange())
								.map(range -> mileage + range));
	}

	//public Optional<Period> getNextDateRelativePeriod(Tracker tracker) {
	//	return getNextDate(tracker).map(nextDate -> Period.between(MainService.getDateToday(), nextDate));
	//}

	public Optional<Long> getNextDateRelativeDays(Tracker tracker) {
		return getNextDate(tracker).map(nextDate -> MainService.getDateToday().until(nextDate, ChronoUnit.DAYS));
	}

	public Optional<Integer> getNextMileageRelative(Tracker tracker) {
		return getNextMileage(tracker).map(nextMileage -> nextMileage - vehicle.getMileage());
	}

	public Status getDateStatus(Tracker tracker) {
		Optional<Long> days = getNextDateRelativeDays(tracker);

		if (days.isEmpty())
			return Status.UNKNOWN;
		if (days.get() < 0)
			return Status.OVERDUE;
		if (days.get() < 90)
			return Status.APPROACHING;
		else
			return Status.OK;
	}

	public Status getMileageStatus(Tracker tracker) {
		Optional<Integer> distance = getNextMileageRelative(tracker);

		if (distance.isEmpty())
			return Status.UNKNOWN;
		if (distance.get() < 0)
			return Status.OVERDUE;
		if (distance.get() < 5000)
			return Status.APPROACHING;
		else
			return Status.OK;
	}

	public <T> ComponentRenderer<HorizontalLayout, Tracker> render(Function<Tracker, Status> statusLoader, Function<Tracker, Optional<T>> dataLoader) {
		return render(statusLoader, dataLoader, String::valueOf);
	}

	public <T> ComponentRenderer<HorizontalLayout, Tracker> render(Function<Tracker, Status> statusLoader, Function<Tracker, Optional<T>> dataLoader, Function<T, String> dataFormatter) {
		return new ComponentRenderer<>(tracker -> {

			Optional<String> str = dataLoader.apply(tracker).map(dataFormatter);
			Status status = statusLoader.apply(tracker);

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
