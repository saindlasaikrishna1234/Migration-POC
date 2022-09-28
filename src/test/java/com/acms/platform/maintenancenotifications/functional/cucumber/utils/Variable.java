package com.acms.platform.maintenancenotifications.functional.cucumber.utils;

import java.util.ArrayList;
import java.util.List;

public class Variable {
    private final String key;
    private final String value;

    public Variable(String key, String value) {
        this.key = key;
        this.value = value;
    }

    String getKey() {
        return key;
    }

    String getValue() {
        return value;
    }

    public static Variable[] get(String key, String value) {
        return init().add(key, value).get();
    }

    public static Builder init(Variable... variables) {
        return new Builder(variables);
    }

    public static class Builder {
        private final List<Variable> variables = new ArrayList<>();

        private Builder(Variable... variables) {
            for (Variable variable : variables) {
                this.variables.add(variable);
            }
        }

        public Builder add(String key, String value) {
            variables.add(new Variable(key, value));
            return this;
        }

        public Variable[] get() {
            Variable[] array = this.variables.toArray(new Variable[this.variables.size()]);
            variables.clear();
            return array;
        }
    }
}
