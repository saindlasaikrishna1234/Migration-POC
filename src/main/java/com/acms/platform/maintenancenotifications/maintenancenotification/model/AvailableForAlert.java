package com.acms.platform.maintenancenotifications.maintenancenotification.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class AvailableForAlert implements Serializable {

    private boolean notifiable;
    private String idNotificationRange;
    private String ruleKey;
    private List<Metadata> metadata;

    public AvailableForAlert() {
    }

    public boolean isNotifiable() {
        return notifiable;
    }

    public AvailableForAlert setNotifiable(boolean notifiable) {
        this.notifiable = notifiable;
        return this;
    }

    public String getIdNotificationRange() {
        return idNotificationRange;
    }

    public AvailableForAlert setIdNotificationRange(String idNotificationRange) {
        this.idNotificationRange = idNotificationRange;
        return this;
    }

    public String getRuleKey() {
        return ruleKey;
    }

    public AvailableForAlert setRuleKey(String ruleKey) {
        this.ruleKey = ruleKey;
        return this;
    }

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public AvailableForAlert setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailableForAlert that = (AvailableForAlert) o;
        return notifiable == that.notifiable &&
                Objects.equals(idNotificationRange, that.idNotificationRange) &&
                Objects.equals(ruleKey, that.ruleKey) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notifiable, idNotificationRange, ruleKey, metadata);
    }
}
