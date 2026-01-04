package com.example.application;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class Notify {

	public static void ok(String msg) {
		var notif = Notification.show(msg);
		notif.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
	}

	public static void debug(String msg) {
		System.out.println(msg);
		var notif = Notification.show(msg);
		notif.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
	}

	public static void error(String msg) {
		System.err.println(msg);
		var notif = Notification.show(msg);
		notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
	}
}
