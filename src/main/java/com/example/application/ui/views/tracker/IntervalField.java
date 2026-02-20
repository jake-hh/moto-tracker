package com.example.application.ui.views.tracker;

import com.example.application.data.BasicInterval;
import com.example.application.data.BasicInterval.Unit;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.value.ValueChangeMode;


public class IntervalField extends CustomField<BasicInterval> {

	private final IntegerField amountField = new IntegerField();
	private final ComboBox<Unit> unitField = new ComboBox<>();


	public IntervalField(String label) {
		setLabel(label);

		amountField.setHelperText("Amount");
		amountField.setValueChangeMode(ValueChangeMode.LAZY);
		amountField.setStepButtonsVisible(true);
		amountField.setStep(1);
		amountField.setMin(BasicInterval.AMOUNT_MIN);
		amountField.setMax(BasicInterval.AMOUNT_MAX);

		amountField.setI18n(
				new IntegerField.IntegerFieldI18n()
						.setMinErrorMessage(BasicInterval.AMOUNT_MIN_MSG)
						.setMaxErrorMessage(BasicInterval.AMOUNT_MAX_MSG)
		);

		unitField.setHelperText("Unit");
		unitField.setItems(Unit.values());

		amountField.addValueChangeListener(e -> updateValue());
		unitField.addValueChangeListener(e -> updateValue());

		var bar = new HorizontalLayout(amountField, unitField);
		bar.setSpacing(true);
		add(bar);
	}

	@Override
	protected BasicInterval generateModelValue() {
		Integer amount = amountField.getValue();
		BasicInterval.Unit unit = unitField.getValue();

		if (amount == null && unit == null)
			return null;
		else if (amount == null || unit == null)
			return new BasicInterval(amount, unit);
		else
			return getNormalizedInterval(amount, unit);
	}

	@Override
	protected void setPresentationValue(BasicInterval value) {
		if (value == null) {
			amountField.clear();
			unitField.clear();
		} else {
			amountField.setValue(value.amount());
			unitField.setValue(value.unit());
		}
	}

	private BasicInterval getNormalizedInterval(Integer amount, Unit unit) {
		// does not check for null reference

		if (unit == Unit.Days) {
			if (amount % 365 == 0) {
				amount /= 365;
				unit = Unit.Years;
			}

			else if (amount % 366 == 0) {
				amount /= 366;
				unit = Unit.Years;
			}

			else if (amount % 30 == 0) {
				amount /= 30;
				unit = Unit.Months;
			}

			else if (amount % 31 == 0) {
				amount /= 31;
				unit = Unit.Months;
			}

			else if (amount % 7 == 0) {
				amount /= 7;
				unit = Unit.Weeks;
			}
		}

		if (unit == Unit.Weeks) {
			if (amount % 52 == 0) {
				amount /= 52;
				unit = Unit.Years;
			}

			if (amount % 51 == 0) {
				amount /= 51;
				unit = Unit.Years;
			}

			if (amount % 50 == 0) {
				amount /= 50;
				unit = Unit.Years;
			}
		}

		if (unit == Unit.Months) {
			if (amount % 12 == 0) {
				amount /= 12;
				unit = Unit.Years;
			}
		}

		return new BasicInterval(amount, unit);
	}
}
