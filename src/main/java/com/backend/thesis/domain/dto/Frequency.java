package com.backend.thesis.domain.dto;

public enum Frequency {
    HOURLY("hourly"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    QUARTERLY("quarterly"),
    YEARLY("yearly");

    public final String label;

    Frequency(String label) {
        this.label = label;
    }

    public String toString() {
        return this.label;
    }
}
