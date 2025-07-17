package ru.shun.arasakafabric.modules.imple.movement;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import ru.shun.arasakafabric.event.impl.EventTick;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
import ru.shun.arasakafabric.ui.imple.NumberSetting;
@ModuleInform(
        name = "Speed",
        category = Category.MOVEMENT,
        description = "All Rights"
)
public class Speed extends Module {
    NumberSetting speed = new NumberSetting("Скорость", 1.2D, 1.0D, 5D, 0.1D);
    public Speed() {
        addSetting(speed);
    }
    @Subscribe
    public void onTickEvent(EventTick e) {
        if (isEnabled()) {
            applySpeed();
        }
    }
    @Override
    public void onTick() {
        if (isEnabled()) {
            applySpeed();
        }
    }
    private void applySpeed() {
        if (mc.world == null || mc.player == null) return;
        for (Entity entity : mc.world.getEntities()) {
            if (mc.player.getBoundingBox().intersects(entity.getBoundingBox())) {
                mc.player.setVelocity(mc.player.getVelocity().multiply(speed.getDoubleValue(), 1.0D, speed.getDoubleValue()));
            }
        }
    }
}

