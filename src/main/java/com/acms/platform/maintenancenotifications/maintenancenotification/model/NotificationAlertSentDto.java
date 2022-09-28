package com.acms.platform.maintenancenotifications.maintenancenotification.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class NotificationAlertSentDto {

    @NotNull
    @Valid
    private NotificationAlertSent notificationAlertSent;

    public NotificationAlertSent getNotificationAlertSent() {
        return notificationAlertSent;
    }

    public NotificationAlertSentDto setNotificationAlertSent(NotificationAlertSent notificationAlertSent) {
        this.notificationAlertSent = notificationAlertSent;
        return this;
    }

    public static class NotificationAlertSent {

        @NotNull
        private Long idNotificationRange;

        public Long getIdNotificationRange() {
            return idNotificationRange;
        }

        public NotificationAlertSent setIdNotificationRange(Long idNotificationRange) {
            this.idNotificationRange = idNotificationRange;
            return this;
        }
    }
}
