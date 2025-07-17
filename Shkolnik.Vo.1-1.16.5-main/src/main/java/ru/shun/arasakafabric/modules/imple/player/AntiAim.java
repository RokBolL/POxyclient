package ru.shun.arasakafabric.modules.imple.player;
import net.minecraft.util.math.MathHelper;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventTick;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
import ru.shun.arasakafabric.ui.imple.NumberSetting;
@ModuleInform(
        name = "AntiAim",
        description = "All Rights",
        category = Category.PLAYER
)
public class AntiAim extends Module {
    private NumberSetting speedRotate;
    private float currentYaw = 0;
    private float targetYaw = 0;
    public AntiAim() {
        speedRotate = new NumberSetting("Speed", 0.1f, 0.1f, 15.0f, 0.1f);
        addSetting(speedRotate);
    }
    @Override
    public void onEnable() {
        EventBus.subscribe(EventTick.class, eventTick -> {
            if (mc.player == null) return;
            if (isEnabled()) {
                targetYaw += 10f;
                if (targetYaw >= 360.0f) {
                    targetYaw -= 360.0f;
                }
                currentYaw = interpolateAngle(currentYaw, targetYaw, (float) speedRotate.getDoubleValue());
                mc.player.prevHeadYaw = mc.player.headYaw;
                mc.player.headYaw = currentYaw;
                mc.player.prevBodyYaw = mc.player.bodyYaw;
                mc.player.bodyYaw = currentYaw;
            }
        });
    }
    private float interpolateAngle(float from, float to, float speed) {
        float diff = MathHelper.wrapDegrees(to - from);
        return from + diff * speed;
    }
    @Override
    public void onTick() {
    }
} 
