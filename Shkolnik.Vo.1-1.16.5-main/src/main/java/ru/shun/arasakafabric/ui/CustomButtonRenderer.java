package ru.shun.arasakafabric.ui;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.systems.RenderSystem;
import ru.shun.arasakafabric.client.util.DrawHelper;
import ru.shun.arasakafabric.client.util.RenderUtil;
import java.awt.Color;
public class CustomButtonRenderer extends DrawableHelper {
    private static final Color BUTTON_NORMAL = new Color(10, 15, 35, 230);    
    private static final Color BUTTON_HOVERED = new Color(5, 8, 20, 255);     
    private static final Color BUTTON_DISABLED = new Color(5, 7, 15, 200);    
    private static final Color TEXT_NORMAL = new Color(150, 180, 255);        
    private static final Color TEXT_DISABLED = new Color(90, 110, 160);       
    private static final Color GLOW_COLOR = new Color(60, 120, 255, 60);      
    private static final int CORNER_RADIUS = 6;  
    private static final int GLOW_SIZE = 5;      
    private static final CustomButtonRenderer INSTANCE = new CustomButtonRenderer();
    public static CustomButtonRenderer getInstance() {
        return INSTANCE;
    }
    public void drawCustomButton(MatrixStack matrices, int x, int y, int width, int height, 
                                String text, boolean hovered, boolean active) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Color buttonColor = active ? (hovered ? BUTTON_HOVERED : BUTTON_NORMAL) : BUTTON_DISABLED;
        Color textColor = active ? TEXT_NORMAL : TEXT_DISABLED;
        if (active && hovered) {
            drawGlowUnderButton(x, y, width, height, CORNER_RADIUS, GLOW_SIZE, GLOW_COLOR);
            DrawHelper.drawRoundedRect(x, y + height, width, height, CORNER_RADIUS, buttonColor);
            Color borderColor = new Color(80, 120, 220, 120);
            DrawHelper.drawRoundedRectOutline(x, y + height, width, height, CORNER_RADIUS, 1.0f, borderColor);
        } else {
            DrawHelper.drawRoundedRect(x, y + height, width, height, CORNER_RADIUS, buttonColor);
            if (active) {
                Color borderColor = new Color(60, 80, 120, 100);
                DrawHelper.drawRoundedRectOutline(x, y + height, width, height, CORNER_RADIUS, 0.8f, borderColor);
            }
        }
        MinecraftClient client = MinecraftClient.getInstance();
        int textX = x + width / 2 - client.textRenderer.getWidth(text) / 2;
        int textY = y + (height - 8) / 2;
        client.textRenderer.drawWithShadow(matrices, text, textX, textY, 
                textColor.getRGB());
    }
    private void drawGlowUnderButton(float x, float y, float width, float height, float radius, float glowSize, Color glowColor) {
        for (int i = 0; i < 3; i++) {
            float currentSize = glowSize - (glowSize / 3) * i;
            float alpha = 0.15f - 0.05f * i;
            if (alpha < 0) alpha = 0;
            Color currentGlowColor = new Color(
                    glowColor.getRed(),
                    glowColor.getGreen(),
                    glowColor.getBlue(),
                    (int)(glowColor.getAlpha() * alpha)
            );
            DrawHelper.drawRoundedRect(
                    x, 
                    y + height + currentSize, 
                    width, 
                    height / 1.5f + currentSize, 
                    radius,
                    currentGlowColor
            );
        }
    }
} 
