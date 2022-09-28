package com.acms.platform.maintenancenotifications.dataretention.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteUserVehicleLinkMessage {
    private Data data;

    public Data getData() {
        return data;
    }

    public DeleteUserVehicleLinkMessage setData(Data data) {
        this.data = data;
        return this;
    }

    public static class Data {
        private String vehicleUuid;

        public String getVehicleUuid() {
            return vehicleUuid;
        }

        public Data setVehicleUuid(String vehicleUuid) {
            this.vehicleUuid = vehicleUuid;
            return this;
        }
    }
}
