package ru.shun.arasakafabric.event;
public class Event {
    private boolean cancelled = false;
    public boolean isCancelled() {
        return cancelled;
    }
    public void cancel() {
        this.cancelled = true;
    }
}

