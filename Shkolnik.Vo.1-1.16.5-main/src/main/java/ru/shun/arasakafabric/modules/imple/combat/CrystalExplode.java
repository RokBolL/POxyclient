package ru.shun.arasakafabric.modules.imple.combat;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.shun.arasakafabric.client.util.RotationUtils;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventTick;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
import ru.shun.arasakafabric.modules.notify.NotificationRenderer;
import ru.shun.arasakafabric.ui.imple.NumberSetting;
import java.util.HashMap;
import java.util.Map;
@ModuleInform(
        name = "CrystalExplode",
        description = "Автоматически устанавливает и взрывает кристалл на обсидиан",
        category = Category.COMBAT
)
public class CrystalExplode extends Module {
    private final NumberSetting delay;
    private final NumberSetting rotationSpeed;
    private BlockPos lastTargetPos = null;
    private final Map<BlockPos, Integer> placedObsidian = new HashMap<>();
    private boolean wasPlacing = false;
    private int cooldownTicks = 0;
    private int scanCooldown = 0;
    private int explodeDelay = 0;
    private BlockPos pendingCrystalPos = null;
    private boolean isRotating = false;
    private float serverYaw = 0;
    private float serverPitch = 0;
    private float targetYaw = 0;
    private float targetPitch = 0;
    private BlockPos rotationTarget = null;
    private boolean lookingAtCrystal = false;
    private float clientYaw = 0;
    private float clientPitch = 0;
    public CrystalExplode() {
        delay = new NumberSetting("Задержка", 2, 0, 20, 1);
        rotationSpeed = new NumberSetting("Скорость поворота", 0.2, 0.05, 1.0, 0.05);
        addSetting(delay);
        addSetting(rotationSpeed);
    }
    @Override
    public void onEnable() {
        NotificationRenderer.add("CrystalExplode включен");
        if (mc.player != null) {
            clientYaw = mc.player.yaw;
            clientPitch = mc.player.pitch;
            serverYaw = clientYaw;
            serverPitch = clientPitch;
        }
        EventBus.subscribe(EventTick.class, eventTick -> {
            if (mc.player == null || mc.world == null || !isEnabled()) return;
            float lastClientYaw = mc.player.yaw;
            float lastClientPitch = mc.player.pitch;
            if (lastClientYaw != clientYaw || lastClientPitch != clientPitch) {
                clientYaw = lastClientYaw;
                clientPitch = lastClientPitch;
            }
            if (isRotating) {
                float prevYaw = serverYaw;
                float prevPitch = serverPitch;
                serverYaw = interpolateAngle(serverYaw, targetYaw, (float) rotationSpeed.getDoubleValue());
                serverPitch = interpolateAngle(serverPitch, targetPitch, (float) rotationSpeed.getDoubleValue());
                boolean rotationChanged = prevYaw != serverYaw || prevPitch != serverPitch;
                if (rotationChanged) {
                    sendRotationPacket(serverYaw, serverPitch);
                    updateThirdPersonModel();
                }
                float yawDiff = Math.abs(getAngleDifference(serverYaw, targetYaw));
                float pitchDiff = Math.abs(getAngleDifference(serverPitch, targetPitch));
                if (yawDiff < 2.0f && pitchDiff < 2.0f) {
                    if (lookingAtCrystal && pendingCrystalPos != null && explodeDelay <= 0) {
                        NotificationRenderer.add("Взрываю кристалл после поворота");
                        explodeCrystal(pendingCrystalPos);
                        pendingCrystalPos = null;
                        isRotating = false;
                        lookingAtCrystal = false;
                    } 
                    else if (!lookingAtCrystal && rotationTarget != null) {
                        NotificationRenderer.add("Размещаю кристалл после поворота");
                        placeAndExplodeCrystal(rotationTarget);
                        isRotating = false;
                        rotationTarget = null;
                    }
                }
            }
            if (cooldownTicks > 0) {
                cooldownTicks--;
                return;
            }
            if (pendingCrystalPos != null) {
                if (explodeDelay <= 0) {
                    if (!isRotating) {
                        rotateToBlock(pendingCrystalPos, true);
                    }
                } else {
                    explodeDelay--;
                }
            }
            if (mc.mouse.wasRightButtonClicked() && mc.player.getMainHandStack().getItem() == Items.OBSIDIAN) {
                if (mc.crosshairTarget instanceof BlockHitResult) {
                    BlockHitResult hitResult = (BlockHitResult) mc.crosshairTarget;
                    lastTargetPos = hitResult.getBlockPos().offset(hitResult.getSide());
                    wasPlacing = true;
                }
            }
            if (wasPlacing && lastTargetPos != null) {
                wasPlacing = false;
                if (mc.world.getBlockState(lastTargetPos).getBlock() == Blocks.OBSIDIAN) {
                    placedObsidian.put(lastTargetPos, (int) delay.getDoubleValue());
                }
            }
            if (scanCooldown <= 0) {
                scanNearbyObsidian();
                scanCooldown = 10; 
            } else {
                scanCooldown--;
            }
            BlockPos nextPos = null;
            int lowestTicks = Integer.MAX_VALUE;
            for (Map.Entry<BlockPos, Integer> entry : placedObsidian.entrySet()) {
                int ticks = entry.getValue();
                if (ticks <= 0 && ticks < lowestTicks) {
                    lowestTicks = ticks;
                    nextPos = entry.getKey();
                }
                entry.setValue(ticks - 1); 
            }
            if (nextPos != null && pendingCrystalPos == null && !isRotating) {
                NotificationRenderer.add("Поворачиваюсь к обсидиану");
                rotateToBlock(nextPos, false);
                rotationTarget = nextPos;
                placedObsidian.remove(nextPos);
                cooldownTicks = 5; 
            }
            restoreClientRotations();
        });
    }
    private void restoreClientRotations() {
        if (mc.player == null) return;
        if (mc.player.yaw != clientYaw || mc.player.pitch != clientPitch) {
            mc.player.yaw = clientYaw;
            mc.player.pitch = clientPitch;
        }
    }
    private void updateThirdPersonModel() {
        if (mc.player == null) return;
        mc.player.prevBodyYaw = mc.player.bodyYaw;
        mc.player.bodyYaw = serverYaw;
        mc.player.prevHeadYaw = mc.player.headYaw;
        mc.player.headYaw = serverYaw;
    }
    private void sendRotationPacket(float yaw, float pitch) {
        if (mc.player == null || mc.player.networkHandler == null) return;
        float originalYaw = mc.player.yaw;
        float originalPitch = mc.player.pitch;
        mc.player.yaw = yaw;
        mc.player.pitch = pitch;
        mc.player.setVelocity(mc.player.getVelocity());
        mc.player.yaw = originalYaw;
        mc.player.pitch = originalPitch;
    }
    private float interpolateAngle(float from, float to, float factor) {
        float diff = MathHelper.wrapDegrees(to - from);
        return from + diff * factor;
    }
    private float getAngleDifference(float first, float second) {
        return MathHelper.wrapDegrees(first - second);
    }
    private void rotateToBlock(BlockPos blockPos, boolean isCrystal) {
        float[] rotations;
        if (isCrystal) {
            Vec3d center = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
            rotations = calculateLookAt(center);
            NotificationRenderer.add("Целевая ротация на кристалл: " + rotations[0] + ", " + rotations[1]);
        } else {
            Vec3d top = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.95, blockPos.getZ() + 0.5);
            rotations = calculateLookAt(top);
            NotificationRenderer.add("Целевая ротация на обсидиан: " + rotations[0] + ", " + rotations[1]);
        }
        targetYaw = rotations[0];
        targetPitch = rotations[1];
        isRotating = true;
        lookingAtCrystal = isCrystal;
        if (!isCrystal) {
            rotationTarget = blockPos;
        }
    }
    private float[] calculateLookAt(Vec3d position) {
        if (mc.player == null) return new float[]{0, 0};
        double eyeX = mc.player.getX();
        double eyeY = mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose());
        double eyeZ = mc.player.getZ();
        double diffX = position.x - eyeX;
        double diffY = position.y - eyeY;
        double diffZ = position.z - eyeZ;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));
        return new float[]{
                MathHelper.wrapDegrees(yaw),
                MathHelper.wrapDegrees(pitch)
        };
    }
    private void scanNearbyObsidian() {
        BlockPos playerPos = mc.player.getBlockPos();
        int scanRadius = 3; 
        for (int x = -scanRadius; x <= scanRadius; x++) {
            for (int z = -scanRadius; z <= scanRadius; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(checkPos).getBlock() == Blocks.OBSIDIAN &&
                        !placedObsidian.containsKey(checkPos) &&
                        mc.world.getBlockState(checkPos.up()).isAir()) {
                        boolean canPlaceCrystal = mc.world.getBlockState(checkPos.up()).isAir() &&
                                                 checkPos.getY() < 255;
                        if (canPlaceCrystal) {
                            placedObsidian.put(checkPos, (int) delay.getDoubleValue());
                            return; 
                        }
                    }
                }
            }
        }
    }
    @Override
    public void onDisable() {
        NotificationRenderer.add("CrystalExplode выключен");
        placedObsidian.clear();
        pendingCrystalPos = null;
        isRotating = false;
    }
    @Override
    public void onTick() {
    }
    private void placeAndExplodeCrystal(BlockPos obsidianPos) {
        if (isRotating && rotationTarget == obsidianPos) {
            float yawDiff = Math.abs(getAngleDifference(serverYaw, targetYaw));
            float pitchDiff = Math.abs(getAngleDifference(serverPitch, targetPitch));
            if (yawDiff >= 2.0f || pitchDiff >= 2.0f) {
                return; 
            }
            isRotating = false;
            rotationTarget = null;
        }
        if (mc.world.getBlockState(obsidianPos).getBlock() != Blocks.OBSIDIAN) {
            return;
        }
        if (!mc.world.getBlockState(obsidianPos.up()).isAir()) {
            return;
        }
        int crystalSlot = findItemInHotbar(Items.END_CRYSTAL);
        if (crystalSlot == -1) {
            NotificationRenderer.add("Нет кристаллов в хотбаре!");
            return;
        }
        int previousSlot = mc.player.inventory.selectedSlot;
        mc.player.inventory.selectedSlot = crystalSlot;
        sendRotationPacket(serverYaw, serverPitch);
        float originalYaw = mc.player.yaw;
        float originalPitch = mc.player.pitch;
        mc.player.yaw = serverYaw;
        mc.player.pitch = serverPitch;
        BlockPos crystalPos = obsidianPos.up();
        Vec3d hitVec = new Vec3d(obsidianPos.getX() + 0.5, obsidianPos.getY() + 1.0, obsidianPos.getZ() + 0.5);
        mc.interactionManager.interactBlock(
                mc.player,
                mc.world,
                Hand.MAIN_HAND,
                new BlockHitResult(hitVec, Direction.UP, obsidianPos, false)
        );
        mc.player.yaw = originalYaw;
        mc.player.pitch = originalPitch;
        mc.player.inventory.selectedSlot = previousSlot;
        NotificationRenderer.add("Поворачиваюсь к кристаллу");
        rotateToBlock(crystalPos, true);
        pendingCrystalPos = crystalPos;
        explodeDelay = 2; 
    }
    private void explodeCrystal(BlockPos crystalPos) {
        sendRotationPacket(serverYaw, serverPitch);
        float originalYaw = mc.player.yaw;
        float originalPitch = mc.player.pitch;
        mc.player.yaw = serverYaw;
        mc.player.pitch = serverPitch;
        mc.world.getEntitiesByClass(EndCrystalEntity.class, mc.player.getBoundingBox().expand(5), 
                entity -> entity.getBlockPos().equals(crystalPos))
                .stream()
                .findFirst()
                .ifPresent(crystal -> mc.interactionManager.attackEntity(mc.player, crystal));
        mc.player.yaw = originalYaw;
        mc.player.pitch = originalPitch;
    }
    private int findItemInHotbar(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStack(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }
}
