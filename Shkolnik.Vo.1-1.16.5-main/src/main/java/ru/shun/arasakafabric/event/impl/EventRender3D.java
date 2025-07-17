package ru.shun.arasakafabric.event.impl;
import net.minecraft.client.util.math.MatrixStack;
import ru.shun.arasakafabric.event.Event;
public class EventRender3D extends Event {
    private final MatrixStack matrixStack;
    private final float tickDelta;
    public EventRender3D(MatrixStack matrixStack, float tickDelta) {
        this.matrixStack = matrixStack;
        this.tickDelta = tickDelta;
    }
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }
    public float getTickDelta() {
        return tickDelta;
    }
}
