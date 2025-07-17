package ru.shun.arasakafabric.ui;
public interface ISetting {
    String getName();
    default Object getValue() {
        return null;
    }
    default void fromString(String value) {
    }
}

