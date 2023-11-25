package org.bohdan.answers.store.entities;

public enum Role {
    USER("User"),
    ADMIN("Admin");

    private final String displayValue;

    private Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
