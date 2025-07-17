package ru.shun.arasakafabric.modules.imple.movement;
import ru.shun.arasakafabric.event.EventBus;
import ru.shun.arasakafabric.event.impl.EventTick;
import ru.shun.arasakafabric.modules.Category;
import ru.shun.arasakafabric.modules.Module;
import ru.shun.arasakafabric.modules.ModuleInform;
@ModuleInform(
        name = "AutoSprint",
        description = "All Rights",
        category = Category.MOVEMENT
)
public  class AutoSprint extends Module {
    @Override
    public void onEnable() {
        EventBus.subscribe(EventTick.class, eventTick -> {
            if (mc.player == null) return;
            if (isEnabled()) {
                if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking()) {
                    mc.player.setSprinting(true);
                }
            } else {
                mc.player.setSprinting(false);
            }
        });
    }
    @Override
    public void onTick() {
    }
} 
