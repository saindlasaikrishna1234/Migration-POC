package com.acms.platform.maintenancenotifications.repository.entities;

import javax.persistence.*;

@Entity
@Table(name = "c_notification_range")
public class NotificationRangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "range_distance")
    private long range;

    @Column(name = "alert")
    private String alert;

    @Column(name = "remaining_time")
    private Integer remainingTimeInDays;

    public long getId() {
        return id;
    }

    public NotificationRangeEntity setId(long id) {
        this.id = id;
        return this;
    }

    public NotificationRangeEntity setRange(long range) {
        this.range = range;
        return this;
    }

    public String getAlert() {
        return alert;
    }

    public NotificationRangeEntity setAlert(String alert) {
        this.alert = alert;
        return this;
    }

    public Integer getRemainingTime() {
        return remainingTimeInDays;
    }

    public NotificationRangeEntity setRemainingTime(Integer remainingTime) {
        this.remainingTimeInDays = remainingTime;
        return this;
    }

    public long getRange(){
        return this.range;
    }
}
