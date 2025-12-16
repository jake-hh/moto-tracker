package com.example.application.views.tracker;

import com.example.application.data.BasicInterval;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

public class IntervalField extends CustomField<BasicInterval> {
	private final IntegerField amountField = new IntegerField();
	private final ComboBox<BasicInterval.Unit> unitField = new ComboBox<>();

	public IntervalField(String label) {
		setLabel(label);

		amountField.setHelperText("Amount");
		amountField.setStepButtonsVisible(true);
		amountField.setStep(1);
		amountField.setMin(1);

		unitField.setItems(BasicInterval.Unit.values());
		unitField.setHelperText("Unit");

		var layout = new HorizontalLayout(amountField, unitField);
		layout.setSpacing(true);
		add(layout);
	}

	@Override
	protected BasicInterval generateModelValue() {
		Integer amount = amountField.getValue();
		BasicInterval.Unit unit = unitField.getValue();

		if (amount == null && unit == null)
			return null;
		else
			return new BasicInterval(amount, unit);
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
}
