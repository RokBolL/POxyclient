package ru.shun.arasakafabric.client.util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
public class RotationUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static float serverYaw;
    private static float serverPitch;
    private static boolean silentRotation = false;
    public static float[] calculateLookAt(Vec3d position) {
        return calculateLookAt(position.x, position.y, position.z);
    }
    public static float[] calculateLookAt(double posX, double posY, double posZ) {
        PlayerEntity player = mc.player;
        if (player == null) return new float[]{0, 0};
        double eyeX = player.getX();
        double eyeY = player.getY() + player.getEyeHeight(player.getPose());
        double eyeZ = player.getZ();
        double diffX = posX - eyeX;
        double diffY = posY - eyeY;
        double diffZ = posZ - eyeZ;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));
        return new float[]{
                MathHelper.wrapDegrees(yaw),
                MathHelper.wrapDegrees(pitch)
        };
    }
    public static float[] calculateLookAtBlock(BlockPos blockPos) {
        return calculateLookAt(
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5
        );
    }
    public static float[] calculateLookAtEntity(Entity entity) {
        return calculateLookAt(
                entity.getX(),
                entity.getY() + entity.getHeight() / 2,
                entity.getZ()
        );
    }
    public static float interpolateAngle(float from, float to, float factor) {
        float diff = MathHelper.wrapDegrees(to - from);
        return from + diff * factor;
    }
    public static void setRotation(float yaw, float pitch) {
        if (mc.player == null) return;
        float prevYaw = mc.player.prevYaw;
        float prevPitch = mc.player.prevPitch;
        mc.player.yaw = yaw;
        mc.player.pitch = pitch;
        mc.player.prevYaw = prevYaw;
        mc.player.prevPitch = prevPitch;
    }
    public static void lookAt(Vec3d position) {
        float[] rotations = calculateLookAt(position);
        setRotation(rotations[0], rotations[1]);
    }
    public static void lookAtBlock(BlockPos blockPos) {
        float[] rotations = calculateLookAtBlock(blockPos);
        setRotation(rotations[0], rotations[1]);
    }
    public static void lookAtEntity(Entity entity) {
        float[] rotations = calculateLookAtEntity(entity);
        setRotation(rotations[0], rotations[1]);
    }
    public static float getAngleDifference(float first, float second) {
        return MathHelper.wrapDegrees(first - second);
    }
    public static boolean isInFOV(Entity entity, float fov) {
        if (mc.player == null) return false;
        float[] rotations = calculateLookAtEntity(entity);
        float yawDiff = Math.abs(getAngleDifference(mc.player.yaw, rotations[0]));
        float pitchDiff = Math.abs(getAngleDifference(mc.player.pitch, rotations[1]));
        return yawDiff <= fov && pitchDiff <= fov;
    }
    public static Vec3d getPlayerLookVec() {
        if (mc.player == null) return Vec3d.ZERO;
        float yaw = mc.player.yaw;
        float pitch = mc.player.pitch;
        float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float pitchCos = MathHelper.cos(-pitch * 0.017453292F);
        float pitchSin = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
    public static void setSilentRotation(boolean enabled) {
        silentRotation = enabled;
        if (!enabled && mc.player != null) {
            serverYaw = mc.player.yaw;
            serverPitch = mc.player.pitch;
        }
    }
    public static boolean isSilentRotationEnabled() {
        return silentRotation;
    }
    public static void setServerRotation(float yaw, float pitch) {
        if (mc.player == null) return;
        serverYaw = MathHelper.wrapDegrees(yaw);
        serverPitch = MathHelper.clamp(MathHelper.wrapDegrees(pitch), -90.0F, 90.0F);
        sendRotationPacket();
    }
    public static void lookAtSilent(Vec3d position) {
        float[] rotations = calculateLookAt(position);
        setServerRotation(rotations[0], rotations[1]);
    }
    public static void lookAtBlockSilent(BlockPos blockPos) {
        float[] rotations = calculateLookAtBlock(blockPos);
        setServerRotation(rotations[0], rotations[1]);
    }
    public static void lookAtEntitySilent(Entity entity) {
        float[] rotations = calculateLookAtEntity(entity);
        setServerRotation(rotations[0], rotations[1]);
    }
    public static float getServerYaw() {
        return silentRotation ? serverYaw : (mc.player != null ? mc.player.yaw : 0);
    }
    public static float getServerPitch() {
        return silentRotation ? serverPitch : (mc.player != null ? mc.player.pitch : 0);
    }
    private static void sendRotationPacket() {
        if (mc.player == null) return;
        float clientYaw = mc.player.yaw;
        float clientPitch = mc.player.pitch;
        mc.player.yaw = serverYaw;
        mc.player.pitch = serverPitch;
        mc.player.setVelocity(mc.player.getVelocity());
        mc.player.yaw = clientYaw;
        mc.player.pitch = clientPitch;
    }
    public static void resetServerRotations() {
        if (mc.player == null) return;
        serverYaw = mc.player.yaw;
        serverPitch = mc.player.pitch;
    }
    public static void sendRotationPacket(float yaw, float pitch) {
        if (mc.player == null || mc.player.networkHandler == null) return;
        try {
            Class<?> packetClass = Class.forName("net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookOnly");
            Object packet = packetClass.getConstructor(float.class, float.class, boolean.class)
                .newInstance(yaw, pitch, mc.player.isOnGround());
            mc.player.networkHandler.sendPacket((net.minecraft.network.Packet<?>) packet);
        } catch (Exception e) {
            float oldYaw = mc.player.yaw;
            float oldPitch = mc.player.pitch;
            mc.player.yaw = yaw;
            mc.player.pitch = pitch;
            mc.player.setVelocity(mc.player.getVelocity());
            mc.player.yaw = oldYaw;
            mc.player.pitch = oldPitch;
        }
    }
} 
