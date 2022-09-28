package com.acms.platform.maintenancenotifications.dataretention.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRetentionMessage {

    private String id;

    private Properties properties;

    public DataRetentionMessage setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public String getId() {
        return id;
    }

    public DataRetentionMessage setId(String id) {
        this.id = id;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "DataRetentionMessage{" +
                "type='" + id + '\'' +
                ", data=" + properties +
                '}';
    }

    public static class Properties {

        private String type;
        private int retentionDurationDays;
        private OffsetDateTime timestamp;

        public String getType() {
            return type;
        }

        public Properties setType(String type) {
            this.type = type;
            return this;
        }

        public int getRetentionDurationDays() {
            return retentionDurationDays;
        }

        public Properties setRetentionDurationDays(int retentionDurationDays) {
            this.retentionDurationDays = retentionDurationDays;
            return this;
        }

        public OffsetDateTime getTimestamp() {
            return timestamp;
        }

        public Properties setTimestamp(OffsetDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        @Override
        public String toString() {
            return "Properties{" +
                    "type='" + type + '\'' +
                    ", retentionDurationDays='" + retentionDurationDays + '\'' +
                    '}';
        }
    }
}
