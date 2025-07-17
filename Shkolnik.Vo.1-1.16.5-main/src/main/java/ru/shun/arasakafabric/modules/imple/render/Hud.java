package ru.shun.arasakafabric.modules.imple.render;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import ru.shun.arasakafabric.client.util.DrawHelper;
import ru.shun.arasakafabric.client.util.RenderUtil;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventRender2D;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
import ru.shun.arasakafabric.modules.ModuleManager;
import ru.shun.arasakafabric.ui.imple.BooleanSetting;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import com.mojang.blaze3d.systems.RenderSystem;
@ModuleInform(
        name = "Hud",
        description = "All Rights",
        category = Category.RENDER
)
public class Hud extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = mc.textRenderer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean logoLoaded = true; 
    private final BooleanSetting waterMark;
    private final BooleanSetting arrayList;
    private final BooleanSetting showPing;
    private final BooleanSetting showFps;
    private final BooleanSetting showTime;
    private final BooleanSetting showLogo;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final ZoneId moscowZone = ZoneId.of("Europe/Moscow");
    private final Color darkBlue = new Color(5, 10, 25, 240);        
    private final Color mediumBlue = new Color(10, 20, 45, 230);     
    private final Color brightBlue = new Color(30, 80, 200, 255);    
    private final Color lightBlue = new Color(100, 150, 255, 255);   
    private final Color ultraBlue = new Color(50, 100, 255, 255);    
    private final Color logoMainColor = new Color(30, 100, 255);     
    private final Color logoColor = mediumBlue;
    private final Color infoBlockColor = mediumBlue;
    private final Color bgColor = darkBlue;                          
    private final Color textColor = Color.WHITE;
    private final Color accentTextColor = lightBlue;
    private final Color glowColor = new Color(50, 100, 255, 120);    
    private final Color valueColor = new Color(50, 100, 255, 255);   
    public Hud() {
        waterMark = new BooleanSetting("WaterMark", true);
        arrayList = new BooleanSetting("ArrayList", false);
        showPing = new BooleanSetting("Пинг", true);
        showFps = new BooleanSetting("FPS", true);
        showTime = new BooleanSetting("Время", true);
        showLogo = new BooleanSetting("Логотип", true);
        addSetting(waterMark);
        addSetting(arrayList);
        addSetting(showPing);
        addSetting(showFps);
        addSetting(showTime);
        addSetting(showLogo);
    }
    @Override
    public void onEnable() {
        EventBus.subscribe(EventRender2D.class, eventRender2D -> {
            if (isEnabled()) {
                if (waterMark.isEnabled()) {
                    String playerName = mc.player != null ? mc.player.getEntityName() : "Игрок";
                    int elementHeight = 16;
                    int yPos = elementHeight + 5; 
                    int xOffset = 5; 
                    int spacing = 5; 
                    int logoBlockWidth = 40; 
                    if (showLogo.isEnabled()) {
                        RenderUtil.drawRoundedRectWithGlow(
                            xOffset, 
                            yPos - elementHeight, 
                            logoBlockWidth, 
                            elementHeight, 
                            5, 
                            8, 
                            logoColor, 
                            glowColor
                        );
                        xOffset += logoBlockWidth + spacing; 
                    }
                    int totalWidth = 0;
                    int playerNameWidth = mc.textRenderer.getWidth(playerName) + 16;
                    totalWidth += playerNameWidth;
                    int pingWidth = 0;
                    if (showPing.isEnabled() && mc.player != null && mc.getNetworkHandler() != null) {
                        PlayerListEntry playerEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                        int ping = playerEntry != null ? playerEntry.getLatency() : 0;
                        String pingText = ping + " ms";
                        pingWidth = mc.textRenderer.getWidth(pingText) + 16;
                        totalWidth += pingWidth;
                    }
                    int fpsWidth = 0;
                    int fps = 0;
                    String fpsText = "";
                    if (showFps.isEnabled()) {
                        try {
                            fps = Integer.parseInt(mc.fpsDebugString.split(" ")[0]);
                        } catch (Exception e) {
                        }
                        fpsText = fps + " fps";
                        fpsWidth = mc.textRenderer.getWidth(fpsText) + 16;
                        totalWidth += fpsWidth;
                    }
                    int timeWidth = 0;
                    String timeString = "";
                    if (showTime.isEnabled()) {
                        LocalDateTime moscowTime = LocalDateTime.now(moscowZone);
                        timeString = moscowTime.format(timeFormatter);
                        timeWidth = mc.textRenderer.getWidth(timeString) + 16;
                        totalWidth += timeWidth;
                    }
                    RenderUtil.drawRoundedRectWithGlow(
                        xOffset, 
                        yPos - elementHeight, 
                        totalWidth, 
                        elementHeight, 
                        5, 
                        8, 
                        infoBlockColor, 
                        glowColor
                    );
                    int currentX = xOffset;
                    mc.textRenderer.draw(eventRender2D.getMatrixStack(), playerName, currentX + 8, 8, valueColor.getRGB());
                    currentX += playerNameWidth;
                    if (showPing.isEnabled() && mc.player != null && mc.getNetworkHandler() != null) {
                        PlayerListEntry playerEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
                        int ping = playerEntry != null ? playerEntry.getLatency() : 0;
                        String pingText = ping + " ms";
                        mc.textRenderer.draw(eventRender2D.getMatrixStack(), " |", currentX, 8, valueColor.getRGB());
                        currentX += mc.textRenderer.getWidth(" |");
                        mc.textRenderer.draw(eventRender2D.getMatrixStack(), " " + pingText, currentX, 8, valueColor.getRGB());
                        currentX += pingWidth - 10; 
                    }
                    if (showFps.isEnabled()) {
                        mc.textRenderer.draw(eventRender2D.getMatrixStack(), " |", currentX, 8, valueColor.getRGB());
                        currentX += mc.textRenderer.getWidth(" |");
                        mc.textRenderer.draw(eventRender2D.getMatrixStack(), " fps " + fps, currentX, 8, valueColor.getRGB());
                        currentX += fpsWidth - 5; 
                    }
                    if (showTime.isEnabled()) {
                        mc.textRenderer.draw(eventRender2D.getMatrixStack(), " |", currentX, 8, valueColor.getRGB());
                        currentX += mc.textRenderer.getWidth(" |");
                        mc.textRenderer.draw(eventRender2D.getMatrixStack(), " " + timeString, currentX, 8, valueColor.getRGB());
                    }
                }
                if (arrayList.isEnabled()) {
                    AtomicInteger y = new AtomicInteger(30);
                    ModuleManager.getModules().stream()
                            .filter(Module::isEnabled)
                            .sorted((m1, m2) -> Integer.compare(mc.textRenderer.getWidth(m2.getName()), mc.textRenderer.getWidth(m1.getName())))
                            .forEach(m -> {
                                int moduleWidth = mc.textRenderer.getWidth(m.getName()) + 8; 
                                RenderUtil.drawRoundedRectWithGlow(
                                    5,
                                    y.get(),
                                    moduleWidth,
                                    15,
                                    4, 
                                    6, 
                                    bgColor,
                                    glowColor
                                );
                                mc.textRenderer.draw(eventRender2D.getMatrixStack(), m.getName(), 9, y.get() + 4, brightBlue.getRGB());
                                y.addAndGet(18); 
                            });
                }
            }
        });
    }
    @Override
    public void onTick() {
    }
    public void onRender2D() {
        if (mc.player == null) return;
        float x = 5;  
        float y = 5;  
        float spacing = 8;  
        float logoBlockWidth = 40;  
        float logoBlockHeight = 20;  
        if (showLogo.isEnabled()) {
            RenderUtil.drawRoundedRectWithGlow(
                x, 
                y, 
                logoBlockWidth, 
                logoBlockHeight, 
                5, 
                8, 
                logoColor, 
                glowColor
            );
        }
        String playerName = mc.player.getGameProfile().getName();
        String pingText = "PING: ";
        int ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
        String pingValue = ping + " ms";
        int fps = 0;
        try {
            fps = Integer.parseInt(mc.fpsDebugString.split(" ")[0]);
        } catch (Exception e) {
        }
        String fpsText = "FPS: ";
        String fpsValue = fps + "";
        String timeText = "TIME: ";
        String timeValue = dateFormat.format(new Date());
        float playerNameWidth = textRenderer.getWidth(playerName);
        float pingWidth = textRenderer.getWidth(pingText);
        float pingValueWidth = textRenderer.getWidth(pingValue);
        float fpsWidth = textRenderer.getWidth(fpsText);
        float fpsValueWidth = textRenderer.getWidth(fpsValue);
        float timeWidth = textRenderer.getWidth(timeText);
        float timeValueWidth = textRenderer.getWidth(timeValue);
        float maxTextWidth = Math.max(
            playerNameWidth, 
            Math.max(pingWidth + pingValueWidth, 
                Math.max(fpsWidth + fpsValueWidth, timeWidth + timeValueWidth)
            )
        );
        float infoBlockPadding = 8; 
        float infoBlockWidth = maxTextWidth + (infoBlockPadding * 2);
        float infoBlockHeight = (9 * 4) + (infoBlockPadding * 2) + 4; 
        float infoX = x;
        if (showLogo.isEnabled()) {
            infoX = x + logoBlockWidth + spacing;
        }
        RenderUtil.drawRoundedRectWithGlow(
            infoX, 
            y, 
            infoBlockWidth, 
            infoBlockHeight, 
            5, 
            8, 
            infoBlockColor, 
            glowColor
        );
        float textY = y + infoBlockPadding;
        mc.textRenderer.drawWithShadow(null, playerName, infoX + infoBlockPadding, textY, accentTextColor.getRGB());
        textY += 9 + 2; 
        mc.textRenderer.drawWithShadow(null, pingText, infoX + infoBlockPadding, textY, lightBlue.getRGB());
        mc.textRenderer.drawWithShadow(null, pingValue, infoX + infoBlockPadding + pingWidth, textY, valueColor.getRGB());
        textY += 9 + 2;
        mc.textRenderer.drawWithShadow(null, fpsText, infoX + infoBlockPadding, textY, lightBlue.getRGB());
        mc.textRenderer.drawWithShadow(null, fpsValue, infoX + infoBlockPadding + fpsWidth, textY, valueColor.getRGB());
        textY += 9 + 2;
        mc.textRenderer.drawWithShadow(null, timeText, infoX + infoBlockPadding, textY, lightBlue.getRGB());
        mc.textRenderer.drawWithShadow(null, timeValue, infoX + infoBlockPadding + timeWidth, textY, valueColor.getRGB());
    }
} 
