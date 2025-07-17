package ru.shun.arasakafabric.ui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;
public class CustomButton extends ButtonWidget {
    public CustomButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, button -> {
            if (onPress != null) {
                onPress.onPress((CustomButton)button);
            }
        });
    }
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        CustomButtonRenderer renderer = CustomButtonRenderer.getInstance();
        boolean isHovered = this.isHovered();
        String buttonText = this.getMessage().asString();
        renderer.drawCustomButton(
            matrices, 
            this.x, 
            this.y, 
            this.width, 
            this.height, 
            buttonText, 
            isHovered, 
            this.active
        );
    }
    public interface PressAction {
        void onPress(CustomButton button);
    }
} 
