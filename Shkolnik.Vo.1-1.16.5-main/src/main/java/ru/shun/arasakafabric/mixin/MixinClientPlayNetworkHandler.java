package ru.shun.arasakafabric.mixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.shun.arasakafabric.stats.PlayerStats;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    private static final Pattern KILL_PATTERN = Pattern.compile("(.*) was slain by (.*)");
    @Inject(method = "onGameMessage", at = @At("RETURN"))
    private void onChatMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String message = packet.getMessage().getString();
        Matcher matcher = KILL_PATTERN.matcher(message);
        if (matcher.find()) {
            String killed = matcher.group(1);
            String killer = matcher.group(2);
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && 
                killer.equals(mc.player.getEntityName())) {
                PlayerStats.incrementKills();
            }
        }
    }
} 
