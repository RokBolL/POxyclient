package ru.shun.arasakafabric.modules.notify;
public class Notification {
    private final String text;
    private int ticksLeft;
    public Notification(String text, int durationTicks) {
        this.text = text;
        this.ticksLeft = durationTicks;
    }
    public String getText() {
        return text;
    }
    public int getTicksLeft() {
        return ticksLeft;
    }
    public void tick() {
        ticksLeft--;
    }
    public boolean isExpired() {
        return ticksLeft <= 0;
    }
}
