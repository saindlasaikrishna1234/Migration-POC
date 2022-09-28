package com.acms.platform.maintenancenotifications.repository.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "c_vehicle_param")
public class VehicleParamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_uuid")
    private String vehicleUuid;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "creation_date")
    private OffsetDateTime creationDate;

    @Column(name = "modification_date")
    private OffsetDateTime modificationDate;

    @Column(name = "remaining_mileage")
    private Long remainingMileage;

    public Long getId() {
        return id;
    }

    public VehicleParamEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getVehicleUuid() {
        return vehicleUuid;
    }

    public VehicleParamEntity setVehicleUuid(String vehicleUuid) {
        this.vehicleUuid = vehicleUuid;
        return this;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public VehicleParamEntity setDueDate(LocalDate creationDate) {
        this.dueDate = creationDate;
        return this;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public VehicleParamEntity setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public OffsetDateTime getModificationDate() {
        return modificationDate;
    }

    public VehicleParamEntity setModificationDate(OffsetDateTime sendDate) {
        this.modificationDate = sendDate;
        return this;
    }

    public Long getRemainingMileage() {
        return remainingMileage;
    }

    public VehicleParamEntity setRemainingMileage(Long remainingMileage) {
        this.remainingMileage = remainingMileage;
        return this;
    }

    @PrePersist
    protected void onCreate() {
        this.setCreationDate(OffsetDateTime.now());
        this.setModificationDate(OffsetDateTime.now());
    }
    @PreUpdate
    protected void onUpdate() {
        this.setModificationDate(OffsetDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleParamEntity that = (VehicleParamEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(vehicleUuid, that.vehicleUuid) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(remainingMileage, that.remainingMileage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vehicleUuid, dueDate, remainingMileage);
    }
}
