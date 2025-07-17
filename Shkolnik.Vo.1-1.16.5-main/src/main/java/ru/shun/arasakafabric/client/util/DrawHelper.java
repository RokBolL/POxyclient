package ru.shun.arasakafabric.client.util;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
public class DrawHelper implements Wrapper {
    public static final HashMap<Integer, Integer> glowCache = new HashMap<Integer, Integer>();
    private static final Shader ROUNDED = new Shader("rounded.frag");
    private static final Shader ROUNDED_GRADIENT = new Shader("rounded_gradient.frag");
    private static final Shader ROUNDED_BLURRED = new Shader("rounded_blurred.frag");
    private static final Shader ROUNDED_BLURRED_GRADIENT = new Shader("rounded_blurred_gradient.frag");
    private static final Shader ROUNDED_OUTLINE = new Shader("rounded_outline.frag");
    private static final Shader ROUNDED_TEXTURE = new Shader("rounded_texture.frag");
    public static final int STEPS = 60;
    public static final double ANGLE =  Math.PI * 2 / STEPS;
    public static final int EX_STEPS = 120;
    public static final double EX_ANGLE =  Math.PI * 2 / EX_STEPS;
    public enum Part {
        FIRST_QUARTER(4, Math.PI / 2),
        SECOND_QUARTER(4, Math.PI),
        THIRD_QUARTER(4, 3 * Math.PI / 2),
        FOURTH_QUARTER(4, 0d),
        FIRST_HALF(2, Math.PI / 2),
        SECOND_HALF(2, Math.PI),
        THIRD_HALF(2, 3 * Math.PI / 2),
        FOURTH_HALF(2, 0d);
        private int ratio;
        private double additionalAngle;
        private Part(int ratio, double addAngle) {
            this.ratio = ratio;
            this.additionalAngle = addAngle;
        }
    }
    public static void drawCircle(double x, double y, double radius, Color color) {
        drawSetup();
        applyColor(color);
        glBegin(GL_TRIANGLE_FAN);
        for(int i = 0; i <= STEPS; i++) {
            glVertex2d(x + radius * Math.sin(ANGLE * i),
                    y + radius * Math.cos(ANGLE * i)
            );
        }
        glEnd();
        glLineWidth(1.5f);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_LINE_LOOP);
        for(int i = 0; i <= STEPS; i++) {
            glVertex2d(x + radius * Math.sin(ANGLE * i),
                    y + radius * Math.cos(ANGLE * i)
            );
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        drawFinish();
    }
    public static void drawCircle(double x, double y, double radius, int progress, int direction, Color color) {
        double angle1 = direction == 0 ? ANGLE : -ANGLE;
        float steps = (STEPS / 100f) * progress;
        drawSetup();
        GlStateManager.disableCull();
        applyColor(color);
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(x, y);
        for(int i = 0; i <= steps; i++) {
            glVertex2d(x + radius * Math.sin(angle1 * i),
                    y + radius * Math.cos(ANGLE * i)
            );
        }
        glEnd();
        glLineWidth(1.5f);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_LINE_LOOP);
        glVertex2d(x, y);
        for(int i = 0; i <= steps; i++) {
            glVertex2d(x + radius * Math.sin(angle1 * i),
                    y + radius * Math.cos(ANGLE * i)
            );
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableCull();
        drawFinish();
    }
    public static void drawCirclePart(double x, double y, double radius, Part part, Color color) {
        double angle = ANGLE / part.ratio;
        drawSetup();
        applyColor(color);
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(x, y);
        for(int i = 0; i <= STEPS; i++) {
            glVertex2d(x + radius * Math.sin(part.additionalAngle + angle * i),
                    y + radius * Math.cos(part.additionalAngle + angle * i)
            );
        }
        glEnd();
        glLineWidth(1.5f);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_LINE_LOOP);
        glVertex2d(x, y);
        for(int i = 0; i <= STEPS; i++) {
            glVertex2d(x + radius * Math.sin(part.additionalAngle + angle * i),
                    y + radius * Math.cos(part.additionalAngle + angle * i)
            );
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        drawFinish();
    }
    public static void drawBlurredCircle(double x, double y, double radius, double blurRadius, Color color) {
        Color transparent = ColorHelper.injectAlpha(color, 0);
        drawSetup();
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0001f);
        glShadeModel(GL_SMOOTH);
        applyColor(color);
        glBegin(GL_TRIANGLE_FAN);
        for(int i = 0; i <= EX_STEPS; i++) {
            glVertex2d(x + radius * Math.sin(EX_ANGLE * i),
                    y + radius * Math.cos(EX_ANGLE * i)
            );
        }
        glEnd();
        glBegin(GL_TRIANGLE_STRIP);
        for (int i = 0; i <= EX_STEPS + 1; i++) {
            if(i % 2 == 1) {
                applyColor(transparent);
                glVertex2d(x + (radius + blurRadius) * Math.sin(EX_ANGLE * i),
                        y + (radius + blurRadius) * Math.cos(EX_ANGLE * i));
            } else {
                applyColor(color);
                glVertex2d(x + radius * Math.sin(EX_ANGLE * i),
                        y + radius * Math.cos(EX_ANGLE * i));
            }
        }
        glEnd();
        glShadeModel(GL_FLAT);
        glDisable(GL_ALPHA_TEST);
        drawFinish();
    }
    public static void drawCircleOutline(double x, double y, double radius, float thikness, Color color) {
        drawSetup();
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(thikness);
        applyColor(color);
        glBegin(GL_LINE_LOOP);
        for(int i = 0; i <= STEPS; i++) {
            glVertex2d(x + radius * Math.sin(ANGLE * i),
                    y + radius * Math.cos(ANGLE * i)
            );
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        drawFinish();
    }
    public static void drawCircleOutline(double x, double y, double radius, float thikness, int progress, int direction, Color color) {
        double angle1 = direction == 0 ? ANGLE : -ANGLE;
        float steps = (STEPS / 100f) * progress;
        drawSetup();
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(thikness);
        applyColor(color);
        glBegin(GL_LINE_STRIP);
        for(int i = 0; i <= steps; i++) {
            glVertex2d(x + radius * Math.sin(angle1 * i),
                    y + radius * Math.cos(ANGLE * i)
            );
        }
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        drawFinish();
    }
    public static void drawRainbowCircle(double x, double y, double radius, double blurRadius) {
        drawSetup();
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0001f);
        glShadeModel(GL_SMOOTH);
        applyColor(Color.WHITE);
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(x, y);
        for(int i = 0; i <= EX_STEPS; i++) {
            applyColor(Color.getHSBColor((float)i / EX_STEPS, 1f, 1f));
            glVertex2d(x + radius * Math.sin(EX_ANGLE * i),
                    y + radius * Math.cos(EX_ANGLE * i)
            );
        }
        glEnd();
        glBegin(GL_TRIANGLE_STRIP);
        for(int i = 0; i <= EX_STEPS + 1; i++) {
            if(i % 2 == 1) {
                applyColor(ColorHelper.injectAlpha(Color.getHSBColor((float)i / EX_STEPS, 1f, 1f), 0));
                glVertex2d(x + (radius + blurRadius) * Math.sin(EX_ANGLE * i),
                        y + (radius + blurRadius) * Math.cos(EX_ANGLE * i));
            } else {
                applyColor(Color.getHSBColor((float)i / EX_STEPS, 1f, 1f));
                glVertex2d(x + radius * Math.sin(EX_ANGLE * i),
                        y + radius * Math.cos(EX_ANGLE * i));
            }
        }
        glEnd();
        glShadeModel(GL_FLAT);
        glDisable(GL_ALPHA_TEST);
        drawFinish();
    }
    public static void drawRect(double x, double y, double width, double height, Color color) {
        drawSetup();
        applyColor(color);
        glBegin(GL_QUADS);
        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x + width, y - height);
        glVertex2d(x, y - height);
        glEnd();
        drawFinish();
    }
    public static void drawGradientRect(double x, double y, double width, double height, Color... clrs) {
        drawSetup();
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);
        applyColor(clrs[1]);
        glVertex2d(x, y);
        applyColor(clrs[2]);
        glVertex2d(x + width, y);
        applyColor(clrs[3]);
        glVertex2d(x + width, y - height);
        applyColor(clrs[0]);
        glVertex2d(x, y - height);
        glEnd();
        glShadeModel(GL_FLAT);
        drawFinish();
    }
    public static void drawRoundedRect(double x, double y, double width, double height, double radius, Color color) {
        float[] c = ColorHelper.getColorComps(color);
        drawSetup();
        ROUNDED.load();
        ROUNDED.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED.setUniformf("round", (float)radius * 2);
        ROUNDED.setUniformf("color", c[0], c[1], c[2], c[3]);
        Shader.draw(x, y - height, width, height);
        ROUNDED.unload();
        drawFinish();
    }
    public static void drawRoundedGradientRect(double x, double y, double width, double height, double radius, Color... colors) {
        float[] c = ColorHelper.getColorComps(colors[0]);
        float[] c1 = ColorHelper.getColorComps(colors[1]);
        float[] c2 = ColorHelper.getColorComps(colors[2]);
        float[] c3 = ColorHelper.getColorComps(colors[3]);
        drawSetup();
        ROUNDED_GRADIENT.load();
        ROUNDED_GRADIENT.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED_GRADIENT.setUniformf("round", (float)radius * 2);
        ROUNDED_GRADIENT.setUniformf("color1", c[0], c[1], c[2], c[3]);
        ROUNDED_GRADIENT.setUniformf("color2", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_GRADIENT.setUniformf("color3", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_GRADIENT.setUniformf("color4", c3[0], c3[1], c3[2], c3[3]);
        Shader.draw(x, y - height, width, height);
        ROUNDED_GRADIENT.unload();
        drawFinish();
    }
    public static void drawRoundedBlurredRect(double x, double y, double width, double height, double roundR, float blurR, Color color) {
        float[] c = ColorHelper.getColorComps(color);
        drawSetup();
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0001f);
        ROUNDED_BLURRED.load();
        ROUNDED_BLURRED.setUniformf("size", (float)(width + 2 * blurR), (float)(height + 2 * blurR));
        ROUNDED_BLURRED.setUniformf("softness", blurR);
        ROUNDED_BLURRED.setUniformf("radius", (float)roundR);
        ROUNDED_BLURRED.setUniformf("color", c[0], c[1], c[2], c[3]);
        Shader.draw(x - blurR, y - height - blurR, width + blurR * 2, height + blurR * 2);
        ROUNDED_BLURRED.unload();
        glDisable(GL_ALPHA_TEST);
        drawFinish();
    }
    public static void drawRoundedGradientBlurredRect(double x, double y, double width, double height, double roundR, float blurR, Color... colors) {
        float[] c = ColorHelper.getColorComps(colors[0]);
        float[] c1 = ColorHelper.getColorComps(colors[1]);
        float[] c2 = ColorHelper.getColorComps(colors[2]);
        float[] c3 = ColorHelper.getColorComps(colors[3]);
        drawSetup();
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0.0001f);
        ROUNDED_BLURRED_GRADIENT.load();
        ROUNDED_BLURRED_GRADIENT.setUniformf("size", (float)(width + 2 * blurR), (float)(height + 2 * blurR));
        ROUNDED_BLURRED_GRADIENT.setUniformf("softness", blurR);
        ROUNDED_BLURRED_GRADIENT.setUniformf("radius", (float)roundR);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color1", c[0], c[1], c[2], c[3]);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color2", c1[0], c1[1], c1[2], c1[3]);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color3", c2[0], c2[1], c2[2], c2[3]);
        ROUNDED_BLURRED_GRADIENT.setUniformf("color4", c3[0], c3[1], c3[2], c3[3]);
        Shader.draw(x - blurR, y - height - blurR, width + blurR * 2, height + blurR * 2);
        ROUNDED_BLURRED_GRADIENT.unload();
        glDisable(GL_ALPHA_TEST);
        drawFinish();
    }
    public static void drawSmoothRect(double x, double y, double width, double height, Color color) {
        drawRoundedRect(x, y, width, height, 1.5, color);
    }
    public static void drawRoundedRectOutline(double x, double y, double width, double height, double radius, float thickness, Color color) {
        float[] c = ColorHelper.getColorComps(color);
        drawSetup();
        ROUNDED_OUTLINE.load();
        ROUNDED_OUTLINE.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED_OUTLINE.setUniformf("round", (float)radius * 2);
        ROUNDED_OUTLINE.setUniformf("thickness", thickness);
        ROUNDED_OUTLINE.setUniformf("color", c[0], c[1], c[2], c[3]);
        Shader.draw(x, y - height, width, height);
        ROUNDED_OUTLINE.unload();
        drawFinish();
    }
    public static void scissor(double x, double y, double width, double height, double scale, double scaledHeight) {
        glScissor((int)(x * scale),
                (int)((scaledHeight - y) * scale),
                (int)(width * scale),
                (int)(height * scale));
    }
    public static void applyColor(Color color) {
        glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }
    public static void resetColor() {
        glColor4f(1f, 1f, 1f, 1f);
    }
    public static void enableScissor() {
        glEnable(GL_SCISSOR_TEST);
    }
    public static void disableScissor() {
        glDisable(GL_SCISSOR_TEST);
    }
    public static void drawSetup() {
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void drawFinish() {
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        resetColor();
    }
}

