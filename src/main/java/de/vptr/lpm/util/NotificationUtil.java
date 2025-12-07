package de.vptr.lpm.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Utility helper for displaying user notifications (info/warning/error).
 */
public final class NotificationUtil {

    private NotificationUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Shows a success notification.
     *
     * @param message the message to display
     */
    public static void showSuccess(final String message) {
        showNotification(message, NotificationVariant.LUMO_SUCCESS, 3000);
    }

    /**
     * Shows an error notification.
     *
     * @param message the message to display
     */
    public static void showError(final String message) {
        showNotification(message, NotificationVariant.LUMO_ERROR, 5000);
    }

    /**
     * Shows a warning notification.
     *
     * @param message the message to display
     */
    public static void showWarning(final String message) {
        showNotification(message, NotificationVariant.LUMO_CONTRAST, 4000);
    }

    /**
     * Shows an info notification.
     *
     * @param message the message to display
     */
    public static void showInfo(final String message) {
        showNotification(message, NotificationVariant.LUMO_PRIMARY, 3000);
    }

    /**
     * Shows a custom notification with specified duration.
     *
     * @param message  the message to display
     * @param variant  the notification variant
     * @param duration duration in milliseconds
     */
    public static void showNotification(final String message, final NotificationVariant variant,
            final int duration) {
        final var notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setText(message);
        notification.setDuration(duration);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }

    /**
     * Shows a notification with custom position.
     *
     * @param message  the message to display
     * @param variant  the notification variant
     * @param duration duration in milliseconds
     * @param position the position of the notification
     */
    public static void showNotification(final String message, final NotificationVariant variant,
            final int duration, final Notification.Position position) {
        final var notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setText(message);
        notification.setDuration(duration);
        notification.setPosition(position);
        notification.open();
    }
}
