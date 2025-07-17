package ru.shun.arasakafabric.mixin;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.lwjgl.opengl.GL11;
import ru.shun.arasakafabric.ui.CustomButton;
import ru.shun.arasakafabric.ui.CustomButtonRenderer;
import ru.shun.arasakafabric.client.ui.AltManagerScreen;
@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {
    @Shadow private boolean doBackgroundFade;
    private static final int TOP_COLOR = 0xff180018;      
    private static final int BOTTOM_COLOR = 0xff000828;   
    private static final int CENTER_COLOR = 0xff000850;   
    private static final int EDGE_COLOR = 0xff000000;     
    protected MixinTitleScreen(Text title) {
        super(title);
    }
    @Inject(method = "isPauseScreen", at = @At("HEAD"), cancellable = true)
    private void disablePanorama(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
    @Inject(method = "init", at = @At("HEAD"))
    private void replaceButtons(CallbackInfo ci) {
        this.buttons.clear();
        this.children.clear();
    }
    @Inject(method = "init", at = @At("RETURN"))
    private void addCustomButtons(CallbackInfo ci) {
        int centerX = this.width / 2;
        int startY = (int) (this.height / 2.5);       
        int buttonWidth = 240;              
        int buttonHeight = 30;              
        int spacing = 38;                   
        this.buttons.clear();
        this.children.clear();
        this.addButton(new CustomButton(
            centerX - buttonWidth / 2, startY, 
            buttonWidth, buttonHeight, 
            Text.of("ОДИНОЧНАЯ ИГРА"), 
            button -> MinecraftClient.getInstance().openScreen(
                new net.minecraft.client.gui.screen.world.SelectWorldScreen(this))
        ));
        this.addButton(new CustomButton(
            centerX - buttonWidth / 2, startY + spacing, 
            buttonWidth, buttonHeight, 
            Text.of("МУЛЬТИПЛЕЕР"), 
            button -> MinecraftClient.getInstance().openScreen(
                new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen(this))
        ));
        this.addButton(new CustomButton(
            centerX - buttonWidth / 2, startY + spacing * 2, 
            buttonWidth, buttonHeight, 
            Text.of("НАСТРОЙКИ"), 
            button -> MinecraftClient.getInstance().openScreen(
                new OptionsScreen(this, MinecraftClient.getInstance().options))
        ));
        this.addButton(new CustomButton(
            centerX - buttonWidth / 2, startY + spacing * 3, 
            buttonWidth, buttonHeight, 
            Text.of("МЕНЕДЖЕР АЛЬТОВ"), 
            button -> MinecraftClient.getInstance().openScreen(
                new AltManagerScreen(this))
        ));
        this.addButton(new CustomButton(
            centerX - buttonWidth / 2, startY + spacing * 4, 
            buttonWidth, buttonHeight, 
            Text.of("ВЫХОД"), 
            button -> MinecraftClient.getInstance().stop()
        ));
    }
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderCustomScreen(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderDarkGradient(matrices);
        for (ClickableWidget button : this.buttons) {
            button.render(matrices, mouseX, mouseY, delta);
        }
        ci.cancel();
    }
    private void renderDarkGradient(MatrixStack matrices) {
        int centerX = this.width / 2;
        int centerY = (int) (this.height / 2.5); 
        double maxDistance = Math.sqrt(Math.pow(Math.max(centerX, this.width - centerX), 2) + 
                                      Math.pow(Math.max(centerY, this.height - centerY), 2));
        double innerRadius = maxDistance * 0.3; 
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        fill(matrices, 0, 0, this.width, this.height, EDGE_COLOR);
        bufferBuilder.begin(GL11.GL_TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(centerX, centerY, 0)
                    .color((CENTER_COLOR >> 16) & 0xFF, 
                           (CENTER_COLOR >> 8) & 0xFF, 
                           CENTER_COLOR & 0xFF, 
                           (CENTER_COLOR >> 24) & 0xFF)
                    .next();
        int segments = 36; 
        int rings = 8;     
        for (int r = 1; r <= rings; r++) {
            double radius = innerRadius * r / rings;
            float colorFactor = (float) r / rings;
            int ringColor = interpolateColor(CENTER_COLOR, EDGE_COLOR, colorFactor);
            for (int i = 0; i < segments; i++) {
                double angle = Math.PI * 2 * i / segments;
                double x = centerX + Math.cos(angle) * radius;
                double y = centerY + Math.sin(angle) * radius;
                bufferBuilder.vertex(x, y, 0)
                            .color((ringColor >> 16) & 0xFF, 
                                   (ringColor >> 8) & 0xFF, 
                                   ringColor & 0xFF, 
                                   (ringColor >> 24) & 0xFF)
                            .next();
            }
        }
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        renderBackgroundEffects(matrices);
    }
    private int interpolateColor(int color1, int color2, float factor) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a1 = (color1 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;
        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));
        int a = (int) (a1 + factor * (a2 - a1));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    private void renderBackgroundEffects(MatrixStack matrices) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
            GL11.GL_SRC_ALPHA, 
            GL11.GL_ONE_MINUS_SRC_ALPHA, 
            GL11.GL_ONE, 
            GL11.GL_ZERO
        );
        int centerX = this.width / 2;
        int centerY = (int) (this.height / 2.5);
        double maxDistance = Math.sqrt(Math.pow(this.width, 2) + Math.pow(this.height, 2)) / 2;
        int gridSize = 35; 
        int lineWidth = 1;
        int baseLinesColor = 0x30303080; 
        for (int i = 0; i < this.width; i += gridSize) {
            double distX = Math.abs(i - centerX);
            double alpha = Math.max(0, 1.0 - (distX / (this.width / 2)) * 1.2);
            int lineColor = adjustAlpha(baseLinesColor, alpha * 0.5);
            drawVerticalLine(matrices, i, 0, this.height, lineWidth, lineColor);
        }
        for (int j = 0; j < this.height; j += gridSize) {
            double distY = Math.abs(j - centerY);
            double alpha = Math.max(0, 1.0 - (distY / (this.height / 2)) * 1.2);
            int lineColor = adjustAlpha(baseLinesColor, alpha * 0.5);
            drawHorizontalLine(matrices, 0, this.width, j, lineWidth, lineColor);
        }
        RenderSystem.disableBlend();
    }
    private int adjustAlpha(int color, double alphaFactor) {
        int a = (int) ((color >> 24 & 0xFF) * alphaFactor);
        return (Math.min(a, 255) << 24) | (color & 0x00FFFFFF);
    }
    private void drawVerticalLine(MatrixStack matrices, int x, int startY, int endY, int width, int color) {
        fill(matrices, x, startY, x + width, endY, color);
    }
    private void drawHorizontalLine(MatrixStack matrices, int startX, int endX, int y, int height, int color) {
        fill(matrices, startX, y, endX, y + height, color);
    }
}

