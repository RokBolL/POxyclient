package ru.shun.arasakafabric.client.util;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.awt.*;
import java.util.Random;
public class MathUtils {
    private static final Random random = new Random();
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
    public static double clampedLerp(double a, double b, double t) {
        if (t < 0) return a;
        return t > 1 ? b : lerp(a, b, t);
    }
    public static double easeInOut(double a, double b, double t) {
        double x = clamp(t, 0, 1);
        x = -(Math.cos(Math.PI * x) - 1) / 2;
        return lerp(a, b, x);
    }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    public static double distance(BlockPos pos, Entity entity) {
        return distance(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                entity.getX(), entity.getY(), entity.getZ()
        );
    }
    public static double distance(Entity entity1, Entity entity2) {
        return distance(
                entity1.getX(), entity1.getY(), entity1.getZ(),
                entity2.getX(), entity2.getY(), entity2.getZ()
        );
    }
    public static double distance(Vec3d vec1, Vec3d vec2) {
        return distance(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
    }
    public static double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    public static float randomFloat(float min, float max) {
        return min + (max - min) * random.nextFloat();
    }
    public static int randomInt(int min, int max) {
        return min + random.nextInt(max - min);
    }
    public static Color lerpColor(Color color1, Color color2, float t) {
        float factor = clamp(t, 0, 1);
        int r = (int) lerp(color1.getRed(), color2.getRed(), factor);
        int g = (int) lerp(color1.getGreen(), color2.getGreen(), factor);
        int b = (int) lerp(color1.getBlue(), color2.getBlue(), factor);
        int a = (int) lerp(color1.getAlpha(), color2.getAlpha(), factor);
        return new Color(r, g, b, a);
    }
    public static Color hslToRgb(float h, float s, float l) {
        float r, g, b;
        if (s == 0) {
            r = g = b = l; 
        } else {
            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1.0f/3);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f/3);
        }
        return new Color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
    }
    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0f/6) return p + (q - p) * 6 * t;
        if (t < 1.0f/2) return q;
        if (t < 2.0f/3) return p + (q - p) * (2.0f/3 - t) * 6;
        return p;
    }
    public static double[] worldToScreen(Vec3d worldPos, float[] matrices, int width, int height) {
        float[] modelView = new float[16];
        float[] projection = new float[16];
        for (int i = 0; i < 16; i++) {
            modelView[i] = matrices[i];
            projection[i] = matrices[i + 16];
        }
        int[] viewport = {0, 0, width, height};
        float[] winPos = new float[3];
        float[] in = {(float) worldPos.x, (float) worldPos.y, (float) worldPos.z, 1.0f};
        float[] out = new float[4];
        matrixMultiply(modelView, in, out);
        in = out.clone();
        matrixMultiply(projection, in, out);
        if (out[3] == 0.0f) return null;
        out[0] /= out[3];
        out[1] /= out[3];
        out[2] /= out[3];
        winPos[0] = viewport[0] + viewport[2] * (out[0] + 1.0f) / 2.0f;
        winPos[1] = viewport[1] + viewport[3] * (out[1] + 1.0f) / 2.0f;
        winPos[2] = (out[2] + 1.0f) / 2.0f; 
        if (winPos[0] < 0 || winPos[0] > width || winPos[1] < 0 || winPos[1] > height || winPos[2] < 0 || winPos[2] > 1) {
            return null;
        }
        return new double[]{winPos[0], height - winPos[1], winPos[2]};
    }
    private static void matrixMultiply(float[] matrix, float[] in, float[] out) {
        for (int i = 0; i < 4; i++) {
            out[i] = 0;
            for (int j = 0; j < 4; j++) {
                out[i] += matrix[i + j * 4] * in[j];
            }
        }
    }
    public static double toRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }
    public static double toDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }
    public static double inverseLerp(double a, double b, double value) {
        if (a == b) return 0;
        return (value - a) / (b - a);
    }
} 
