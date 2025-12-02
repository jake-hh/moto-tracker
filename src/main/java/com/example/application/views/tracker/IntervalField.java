package com.example.application.views.tracker;

import com.example.application.data.BasicInterval;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;

public class IntervalField extends CustomField<BasicInterval> {
	private final IntegerField amount = new IntegerField();
	private final ComboBox<BasicInterval.Unit> unit = new ComboBox<>();

	public IntervalField(String label) {
		setLabel(label);

		amount.setHelperText("Amount");
		amount.setStepButtonsVisible(true);
		amount.setStep(1);
		amount.setMin(1);

		unit.setItems(BasicInterval.Unit.values());
		unit.setHelperText("Unit");

		var layout = new HorizontalLayout(amount, unit);
		layout.setSpacing(true);
		add(layout);
	}

	@Override
	protected BasicInterval generateModelValue() {
		Integer a = amount.getValue();
		BasicInterval.Unit u = unit.getValue();

		if (a == null || u == null)
			return null;
		else
			return new BasicInterval(a, u);
	}

	@Override
	protected void setPresentationValue(BasicInterval value) {
		if (value == null) {
			amount.clear();
			unit.clear();
		} else {
			amount.setValue(value.amount());
			unit.setValue(value.unit());
		}
	}
}
