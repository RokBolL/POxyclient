package ru.shun.arasakafabric.mixin;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.shun.arasakafabric.client.KeybindHandler;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventTick;
import ru.shun.arasakafabric.modules.notify.NotificationRenderer;
@Mixin(MinecraftClient.class)
public class MixinTick {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        EventBus.post(new EventTick());
        KeybindHandler.handleKeyTick();
        NotificationRenderer.tick();
    }
}
