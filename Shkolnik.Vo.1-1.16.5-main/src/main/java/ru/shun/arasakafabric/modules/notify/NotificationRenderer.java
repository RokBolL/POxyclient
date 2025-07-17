package ru.shun.arasakafabric.modules.notify;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class NotificationRenderer {
    private static final List<Notification> notifications = new ArrayList<>();
    public static void add(String text) {
        notifications.add(new Notification(text, 60)); 
    }
    public static void tick() {
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification n = iterator.next();
            n.tick();
            if (n.isExpired()) {
                iterator.remove();
            }
        }
    }
    public static void render(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer tr = client.textRenderer;
        int y = 10;
        for (Notification n : notifications) {
            tr.drawWithShadow(matrices, n.getText(), client.getWindow().getScaledWidth() / 2 - 60, y, 0xFFFFFF);
            y += 12;
        }
    }
}
