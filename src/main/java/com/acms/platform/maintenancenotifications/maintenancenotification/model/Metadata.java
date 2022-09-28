package com.acms.platform.maintenancenotifications.maintenancenotification.model;

import java.util.Objects;

public class Metadata {
    private final String key;
    private final String value;

    public Metadata(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(key, metadata.key) &&
                Objects.equals(value, metadata.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
