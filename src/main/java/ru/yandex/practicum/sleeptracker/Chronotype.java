package ru.yandex.practicum.sleeptracker;

public enum Chronotype {
    JAY("Жаворонок"),
    OWL("Сова"),
    PIGEON("Голубь");

    private final String description;

    Chronotype(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
