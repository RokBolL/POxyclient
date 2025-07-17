package ru.shun.arasakafabric.mixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.AttackEvent;
@Mixin(MinecraftClient.class)
public class MixinAttackEvent {
    @Inject(method = "doAttack", at = @At("HEAD"))
    private void onAttack(CallbackInfo ci) {
        ClientPlayerEntity player = ((MinecraftClient)(Object)this).player;
        if (player == null || player.getAttacking() == null) return;
        EventBus.post(new AttackEvent(player.getAttacking()));
    }
}

