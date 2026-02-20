package com.example.application.ui;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;


public class Notify {

	public static void ok(String msg) {
		//System.out.println("[Ok]    " + msg);
		var notif = Notification.show(msg);
		//notif.setDuration(15_000);
		notif.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
	}

	public static void debug(String msg) {
		System.out.println("[Debug] " + msg);
		var notif = Notification.show(msg);
		notif.setDuration(15_000);
		notif.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
	}

	public static void error(String msg) {
		System.err.println("[Error] " + msg);
		var notif = Notification.show(msg);
		notif.setDuration(15_000);
		notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
	}
}
