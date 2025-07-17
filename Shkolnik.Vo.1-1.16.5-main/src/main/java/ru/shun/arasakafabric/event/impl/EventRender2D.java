package ru.shun.arasakafabric.event.impl;
import net.minecraft.client.util.math.MatrixStack;
import ru.shun.arasakafabric.event.Event;
public class EventRender2D extends Event {
    private final MatrixStack matrixStack;
    public EventRender2D(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }
}

