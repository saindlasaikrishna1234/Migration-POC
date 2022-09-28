package com.acms.platform.maintenancenotifications.repository.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "c_notification_vehicle")
public class NotificationVehicleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "vehicle_uuid")
    private String vehicleUuid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_range_id", referencedColumnName = "id")
    private NotificationRangeEntity notificationRangeEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_param_id", referencedColumnName = "id")
    private VehicleParamEntity vehicleParamEntity;

    @Column(name = "creation_date")
    private OffsetDateTime creationDate;

    @Column(name = "send_date")
    private OffsetDateTime sendDate;

    @Column(name = "send")
    private boolean send;

    @Column(name = "latest_due_date")
    private LocalDate latestDueDate;

    @Column(name = "latest_due_mileage")
    private String latestDueMileage;

    public long getId() {
        return id;
    }

    public NotificationVehicleEntity setId(long id) {
        this.id = id;
        return this;
    }

    public String getVehicleUuid() {
        return vehicleUuid;
    }

    public NotificationVehicleEntity setVehicleUuid(String vehicleUuid) {
        this.vehicleUuid = vehicleUuid;
        return this;
    }

    public NotificationRangeEntity getNotificationRangeEntity() {
        return notificationRangeEntity;
    }

    public NotificationVehicleEntity setNotificationRangeEntity(NotificationRangeEntity notificationRangeEntity) {
        this.notificationRangeEntity = notificationRangeEntity;
        return this;
    }

    public VehicleParamEntity getVehicleParamEntity() {
        return vehicleParamEntity;
    }

    public NotificationVehicleEntity setVehicleParamEntity(VehicleParamEntity vehicleParamEntity) {
        this.vehicleParamEntity = vehicleParamEntity;
        return this;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public NotificationVehicleEntity setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public OffsetDateTime getSendDate() {
        return sendDate;
    }

    public NotificationVehicleEntity setSendDate(OffsetDateTime sendDate) {
        this.sendDate = sendDate;
        return this;
    }

    public boolean getSend() {
        return send;
    }

    public LocalDate getLatestDueDate() {
        return latestDueDate;
    }

    public NotificationVehicleEntity setLatestDueDate(LocalDate latestDueDate) {
        this.latestDueDate = latestDueDate;
        return this;
    }

    public String getLatestDueMileage() {
        return latestDueMileage;
    }

    public NotificationVehicleEntity setLatestDueMileage(String latestDueMileage) {
        this.latestDueMileage = latestDueMileage;
        return this;
    }

    public NotificationVehicleEntity setSend(boolean send) {
        this.send = send;
        return this;
    }

    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            this.creationDate = OffsetDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationVehicleEntity that = (NotificationVehicleEntity) o;
        return id == that.id &&
                send == that.send &&
                Objects.equals(vehicleUuid, that.vehicleUuid) &&
                Objects.equals(notificationRangeEntity, that.notificationRangeEntity) &&
                Objects.equals(vehicleParamEntity, that.vehicleParamEntity) &&
                Objects.equals(sendDate, that.sendDate) &&
                Objects.equals(latestDueDate, that.latestDueDate) &&
                Objects.equals(latestDueMileage, that.latestDueMileage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vehicleUuid, notificationRangeEntity, vehicleParamEntity, sendDate, send, latestDueDate, latestDueMileage);
    }
}
