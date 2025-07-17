package ru.shun.arasakafabric.client.util;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
public class RenderUtil {
    public static void drawRect(float x, float y, float width, float height, int color) {
        float x2 = x + width;
        float y2 = y + height;
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(r, g, b, a);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);
        buffer.vertex(x, y2, 0).next();
        buffer.vertex(x2, y2, 0).next();
        buffer.vertex(x2, y, 0).next();
        buffer.vertex(x, y, 0).next();
        Tessellator.getInstance().draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    public static void drawGradientRect(float x, float y, float width, float height, int topColor, int bottomColor) {
        float x2 = x + width;
        float y2 = y + height;
        float a1 = (topColor >> 24 & 0xFF) / 255.0F;
        float r1 = (topColor >> 16 & 0xFF) / 255.0F;
        float g1 = (topColor >> 8 & 0xFF) / 255.0F;
        float b1 = (topColor & 0xFF) / 255.0F;
        float a2 = (bottomColor >> 24 & 0xFF) / 255.0F;
        float r2 = (bottomColor >> 16 & 0xFF) / 255.0F;
        float g2 = (bottomColor >> 8 & 0xFF) / 255.0F;
        float b2 = (bottomColor & 0xFF) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x, y2, 0).color(r2, g2, b2, a2).next();
        buffer.vertex(x2, y2, 0).color(r2, g2, b2, a2).next();
        buffer.vertex(x2, y, 0).color(r1, g1, b1, a1).next();
        buffer.vertex(x, y, 0).color(r1, g1, b1, a1).next();
        Tessellator.getInstance().draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    public static void drawRectWithGlow(float x, float y, float width, float height, float glowSize, Color rectColor, Color glowColor) {
        for (int i = 0; i < 6; i++) {
            float currentSize = glowSize - (glowSize / 6) * i;
            float alpha = 0.12f - 0.02f * i;
            if (alpha < 0) alpha = 0;
            Color currentGlowColor = new Color(
                    glowColor.getRed(),
                    glowColor.getGreen(),
                    glowColor.getBlue(),
                    (int)(glowColor.getAlpha() * alpha)
            );
            drawRect(
                    x - currentSize,
                    y - currentSize,
                    width + currentSize * 2,
                    height + currentSize * 2,
                    currentGlowColor.getRGB()
            );
        }
        drawRect(x, y, width, height, rectColor.getRGB());
    }
    public static void drawRoundedRectWithGlow(float x, float y, float width, float height, float radius, float glowSize, Color rectColor, Color glowColor) {
        for (int i = 0; i < 6; i++) {
            float currentSize = glowSize - (glowSize / 6) * i;
            float alpha = 0.12f - 0.02f * i;
            if (alpha < 0) alpha = 0;
            Color currentGlowColor = new Color(
                    glowColor.getRed(),
                    glowColor.getGreen(),
                    glowColor.getBlue(),
                    (int)(glowColor.getAlpha() * alpha)
            );
            DrawHelper.drawRoundedRect(
                    x - currentSize,
                    y + height + currentSize, 
                    width + currentSize * 2,
                    height + currentSize * 2,
                    radius + currentSize / 2,
                    currentGlowColor
            );
        }
        DrawHelper.drawRoundedRect(x, y + height, width, height, radius, rectColor);
    }
    public static void drawCircle(float x, float y, float radius, int color) {
        float a = (color >> 24 & 0xFF) / 255.0F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(r, g, b, a);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.toRadians(i);
            GL11.glVertex2d(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius);
        }
        GL11.glEnd();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        drawGradientRound(x, y, width, height, radius, color, color);
    }
    public static void drawGradientRound(float x, float y, float width, float height, float radius, Color topColor, Color bottomColor) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        putVertex(buffer, x + width / 2f, y + height / 2f, mixColor(topColor, bottomColor));
        for (int i = 0; i <= 360; i += 5) {
            double angle = Math.toRadians(i);
            float dx = (float) (Math.cos(angle) * (width / 2f - radius));
            float dy = (float) (Math.sin(angle) * (height / 2f - radius));
            putVertex(buffer, x + width / 2f + dx, y + height / 2f + dy, i < 180 ? topColor : bottomColor);
        }
        tess.draw();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
    private static void putVertex(BufferBuilder buffer, float x, float y, Color color) {
        buffer.vertex(x, y, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
    }
    private static Color mixColor(Color a, Color b) {
        return new Color(
                (a.getRed() + b.getRed()) / 2,
                (a.getGreen() + b.getGreen()) / 2,
                (a.getBlue() + b.getBlue()) / 2,
                (a.getAlpha() + b.getAlpha()) / 2
        );
    }
}
