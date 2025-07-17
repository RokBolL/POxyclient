package ru.shun.arasakafabric.modules.imple.render;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import ru.shun.arasakafabric.client.FriendManager;
import ru.shun.arasakafabric.client.util.RenderUtil;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventRender3D;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
import ru.shun.arasakafabric.ui.imple.BooleanSetting;
import ru.shun.arasakafabric.ui.imple.NumberSetting;
import java.awt.*;
@ModuleInform(
        name = "NameTags",
        description = "Показывает имена игроков через стены",
        category = Category.RENDER
)
public class NameTags extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final FriendManager friendManager = FriendManager.getInstance();
    private final BooleanSetting showDistance;
    private final BooleanSetting showHealth;
    private final BooleanSetting showPing;
    private final NumberSetting scale;
    private final BooleanSetting showFriend;
    public NameTags() {
        scale = new NumberSetting("Размер", 2.0f, 0.5f, 10.0f, 0.1f);
        showDistance = new BooleanSetting("Дистанция", true);
        showHealth = new BooleanSetting("Здоровье", true);
        showPing = new BooleanSetting("Пинг", true);
        showFriend = new BooleanSetting("Друзья", true);
        addSetting(scale);
        addSetting(showDistance);
        addSetting(showHealth);
        addSetting(showPing);
        addSetting(showFriend);
    }
    @Override
    public void onEnable() {
        EventBus.subscribe(EventRender3D.class, event -> {
            if (isEnabled() && mc.world != null) {
                renderNameTags(event.getMatrixStack(), event.getTickDelta());
            }
        });
    }
    private void renderNameTags(MatrixStack matrices, float tickDelta) {
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                PlayerEntity player = (PlayerEntity) entity;
                String playerName = player.getGameProfile().getName();
                boolean isFriend = friendManager.isFriend(playerName);
                double x = MathHelper.lerp(tickDelta, player.prevX, player.getX()) - cameraPos.getX();
                double y = MathHelper.lerp(tickDelta, player.prevY, player.getY()) - cameraPos.getY() + player.getHeight() + 0.5;
                double z = MathHelper.lerp(tickDelta, player.prevZ, player.getZ()) - cameraPos.getZ();
                matrices.push();
                RenderSystem.disableDepthTest();
                double distance = Math.sqrt(x * x + y * y + z * z);
                float baseScale = (float)scale.getDoubleValue();
                float dynamicScale;
                if (distance < 10) {
                    dynamicScale = baseScale * 0.5f + (float)(0.05f * distance);
                } else if (distance < 50) {
                    dynamicScale = baseScale + (float)((distance - 10) * 0.1f);
                } else {
                    dynamicScale = baseScale * 5.0f;
                }
                float finalScale = 0.025f * dynamicScale;
                matrices.translate(x, y, z);
                matrices.multiply(mc.gameRenderer.getCamera().getRotation());
                matrices.scale(-finalScale, -finalScale, finalScale);
                TextRenderer textRenderer = mc.textRenderer;
                StringBuilder info = new StringBuilder();
                if (showHealth.isEnabled()) {
                    float health = player.getHealth();
                    info.append(getHealthColor(health)).append(String.format("%.1f", health)).append("HP ");
                }
                if (showPing.isEnabled() && mc.getNetworkHandler() != null) {
                    PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
                    if (entry != null) {
                        int ping = entry.getLatency();
                        info.append(getPingColor(ping)).append(ping).append("ms ");
                    }
                }
                if (showDistance.isEnabled()) {
                    info.append("§7").append(String.format("%.1f", distance)).append("m");
                }
                int nameWidth = textRenderer.getWidth(playerName);
                int infoWidth = textRenderer.getWidth(info.toString());
                int maxWidth = Math.max(nameWidth, infoWidth);
                float nameX = -nameWidth / 2.0f;
                float infoX = -infoWidth / 2.0f;
                float padding = 4.0f;
                float nameHeight = textRenderer.fontHeight;
                float infoHeight = info.length() > 0 ? textRenderer.fontHeight : 0;
                float totalHeight = nameHeight + infoHeight + (infoHeight > 0 ? 2 : 0); 
                Color bgColor, borderColor, glowColor;
                if (isFriend && showFriend.isEnabled()) {
                    bgColor = new Color(10, 40, 10, 200);
                    borderColor = new Color(40, 200, 40, 180);
                    glowColor = new Color(30, 180, 30, 70);
                } else {
                    bgColor = new Color(10, 15, 30, 200);
                    borderColor = new Color(40, 100, 240, 180);
                    glowColor = new Color(50, 100, 255, 70);
                }
                float bgX = -maxWidth / 2.0f - padding;
                float bgY = -padding;
                float bgWidth = maxWidth + padding * 2;
                float bgHeight = totalHeight + padding * 2;
                RenderUtil.drawRoundedRectWithGlow(
                    bgX,
                    bgY,
                    bgWidth,
                    bgHeight,
                    4.0f, 
                    6.0f, 
                    bgColor,
                    glowColor
                );
                float borderHeight = 2.0f;
                RenderUtil.drawRect(
                    bgX + 1,  
                    bgY + 1,
                    bgWidth - 2,  
                    borderHeight,
                    borderColor.getRGB()
                );
                int nameColor = isFriend && showFriend.isEnabled()
                    ? new Color(60, 240, 60).getRGB() 
                    : new Color(255, 255, 255).getRGB(); 
                textRenderer.drawWithShadow(matrices, playerName, nameX, 0, nameColor);
                if (info.length() > 0) {
                    textRenderer.drawWithShadow(matrices, info.toString(), infoX, nameHeight + 2, Color.WHITE.getRGB());
                }
                matrices.pop();
                RenderSystem.enableDepthTest();
            }
        }
    }
    private String getHealthColor(float health) {
        if (health > 16.0f) {
            return "§a"; 
        } else if (health > 8.0f) {
            return "§e"; 
        } else {
            return "§c"; 
        }
    }
    private String getPingColor(int ping) {
        if (ping < 50) {
            return "§a"; 
        } else if (ping < 100) {
            return "§e"; 
        } else {
            return "§c"; 
        }
    }
    @Override
    public void onTick() {
    }
} 
