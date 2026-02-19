package com.example.application.data;

import com.vaadin.flow.component.Component;
import org.vaadin.lineawesome.LineAwesomeIcon;


//osobowy PKV, dostawczy LKV

public enum VehicleType {
	Car, Lorry, Motorcycle, Moped, Other;

	public Component getIcon() {
		return switch (this) {
			case Car -> LineAwesomeIcon.CAR_SIDE_SOLID.create();
			case Lorry -> LineAwesomeIcon.TRUCK_SOLID.create();
			case Motorcycle, Moped -> LineAwesomeIcon.MOTORCYCLE_SOLID.create();
			case Other -> LineAwesomeIcon.QUESTION_CIRCLE.create();
		};
	}
}
