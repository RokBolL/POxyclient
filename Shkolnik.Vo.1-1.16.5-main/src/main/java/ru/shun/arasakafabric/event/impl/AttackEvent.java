package ru.shun.arasakafabric.event.impl;
import net.minecraft.entity.Entity;
import ru.shun.arasakafabric.event.Event;
public class AttackEvent extends Event {
    private final Entity target;
    public AttackEvent(Entity target) {
        this.target = target;
    }
    public Entity getTarget() {
        return target;
    }
}

