package ru.shun.arasakafabric.mixin;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventRender2D;
import ru.shun.arasakafabric.modules.notify.NotificationRenderer;
@Mixin(InGameHud.class)
public class MixinRender2D {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        EventBus.post(new EventRender2D(matrices));
        NotificationRenderer.render(matrices);
    }
}

