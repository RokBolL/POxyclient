package ru.shun.arasakafabric.event.impl;
import net.minecraft.entity.player.PlayerEntity;
public class EventPlayerKill {
    private final PlayerEntity killed;
    public EventPlayerKill(PlayerEntity killed) {
        this.killed = killed;
    }
    public PlayerEntity getKilled() {
        return killed;
    }
} 
